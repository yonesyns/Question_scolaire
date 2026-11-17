package com.gestetud.external;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/mock-exam-api/exams")
@Profile("!external-exam-api")
public class MockExternalExamApiController {
    private final AtomicLong sequence=new AtomicLong();
    private final ConcurrentHashMap<String,ExternalExamResponse> exams=new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long,String> sourceIds=new ConcurrentHashMap<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized ExternalExamResponse create(@RequestHeader(value="Idempotency-Key",required=false) String key,
                                       @RequestBody ExternalExamRequest request){
        String existing=sourceIds.get(request.sourceExamId());
        if(existing!=null)return exams.get(existing);
        String id="EXT-"+sequence.incrementAndGet();
        ExternalExamResponse created=new ExternalExamResponse(id,"SCHEDULED",request.title(),request.date());
        exams.put(id,created);sourceIds.put(request.sourceExamId(),id);return created;
    }

    @GetMapping("/{id}")
    public ExternalExamResponse find(@PathVariable String id){return get(id);}

    @PutMapping("/{id}")
    public ExternalExamResponse update(@PathVariable String id,@RequestBody ExternalExamRequest request){
        ExternalExamResponse current=get(id);
        ExternalExamResponse updated=new ExternalExamResponse(id,current.status(),request.title(),request.date());
        exams.put(id,updated);return updated;
    }

    /** Route de démonstration permettant de simuler une modification de statut côté fournisseur externe. */
    @PutMapping("/{id}/status")
    public ExternalExamResponse changeStatus(@PathVariable String id,@RequestBody StatusInput input){
        ExternalExamResponse current=get(id);
        ExternalExamResponse updated=new ExternalExamResponse(id,input.status(),current.title(),current.date());
        exams.put(id,updated);return updated;
    }

    private ExternalExamResponse get(String id){
        ExternalExamResponse response=exams.get(id);
        if(response==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Examen externe introuvable");
        return response;
    }

    public record StatusInput(String status){}
}
