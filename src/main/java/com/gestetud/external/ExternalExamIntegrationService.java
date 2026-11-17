package com.gestetud.external;

import com.gestetud.model.Exam;
import com.gestetud.repo.ExamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ExternalExamIntegrationService {
    private final RestClient client; private final ExternalExamApiProperties properties; private final ExamRepository exams;
    private final ConcurrentMap<Long,Object> syncLocks=new ConcurrentHashMap<>();
    public ExternalExamIntegrationService(RestClient externalExamRestClient,ExternalExamApiProperties properties,ExamRepository exams){
        this.client=externalExamRestClient;this.properties=properties;this.exams=exams;
    }

    public ExternalExamView sync(Long internalId){
        synchronized(syncLocks.computeIfAbsent(internalId,id->new Object())){return syncLocked(internalId);}
    }

    private ExternalExamView syncLocked(Long internalId){
        Exam exam=load(internalId);
        if(!properties.isEnabled()){
            exam.externalSyncStatus=ExternalSyncStatus.DISABLED;exam.externalError=null;exam=exams.save(exam);
            return view(exam,null);
        }
        try{
            ExternalExamRequest request=toRequest(exam);
            ExternalExamResponse response;
            if(exam.externalExamId==null||exam.externalExamId.isBlank()){
                response=client.post().uri("/exams").header("Idempotency-Key","gestetud-exam-"+exam.id)
                        .body(request).retrieve().body(ExternalExamResponse.class);
            }else{
                response=client.put().uri("/exams/{id}",exam.externalExamId).body(request).retrieve().body(ExternalExamResponse.class);
            }
            if(response==null||response.id()==null)throw new IllegalStateException("L'API externe a renvoyé une réponse vide ou sans identifiant");
            apply(exam,response);exam.externalSyncStatus=ExternalSyncStatus.SYNCED;exam.externalError=null;
            exam=exams.save(exam);
            return view(exam,response);
        }catch(Exception error){
            fail(exam,error);return view(exam,null);
        }
    }

    public ExternalExamView consult(Long internalId){
        synchronized(syncLocks.computeIfAbsent(internalId,id->new Object())){return consultLocked(internalId);}
    }

    private ExternalExamView consultLocked(Long internalId){
        Exam exam=load(internalId);
        if(exam.externalExamId==null||exam.externalExamId.isBlank())return syncLocked(internalId);
        if(!properties.isEnabled()){
            exam.externalSyncStatus=ExternalSyncStatus.DISABLED;exams.save(exam);return view(exam,null);
        }
        try{
            ExternalExamResponse response=client.get().uri("/exams/{id}",exam.externalExamId).retrieve().body(ExternalExamResponse.class);
            if(response==null)throw new IllegalStateException("L'API externe a renvoyé une réponse vide");
            apply(exam,response);exam.externalSyncStatus=ExternalSyncStatus.SYNCED;exam.externalError=null;
            exam=exams.save(exam);return view(exam,response);
        }catch(Exception error){
            fail(exam,error);return view(exam,null);
        }
    }

    private Exam load(Long id){
        return exams.findWithDetailsById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Examen introuvable"));
    }
    private ExternalExamRequest toRequest(Exam e){
        return new ExternalExamRequest(e.id,e.course.code,e.title,e.date,e.evaluationType==null?null:e.evaluationType.code,e.finalExam);
    }
    private void apply(Exam exam,ExternalExamResponse response){
        exam.externalExamId=response.id();exam.externalRawStatus=response.status();
        exam.externalStatus=mapStatus(response.status());
    }
    private ExternalExamStatus mapStatus(String status){
        if(status==null)return ExternalExamStatus.UNKNOWN;
        return switch(status.trim().toUpperCase(Locale.ROOT)){
            case "SCHEDULED","PLANNED","OPEN","PENDING"->ExternalExamStatus.SCHEDULED;
            case "CANCELLED","CANCELED","VOID"->ExternalExamStatus.CANCELLED;
            case "COMPLETED","FINISHED","CLOSED"->ExternalExamStatus.COMPLETED;
            default->ExternalExamStatus.UNKNOWN;
        };
    }
    private void fail(Exam exam,Exception error){
        exam.externalSyncStatus=ExternalSyncStatus.FAILED;
        String message=error.getMessage()==null?error.getClass().getSimpleName():error.getMessage();
        exam.externalError=message.substring(0,Math.min(1000,message.length()));
        exams.save(exam);
    }
    private ExternalExamView view(Exam exam,ExternalExamResponse response){
        return new ExternalExamView(exam.id,exam.externalExamId,exam.externalStatus,exam.externalRawStatus,
                exam.externalSyncStatus,exam.externalError,response==null?exam.title:response.title(),
                response==null?exam.date:response.date());
    }
}
