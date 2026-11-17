package com.gestetud;

import com.gestetud.model.*;
import com.gestetud.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    private final StudentRepository students; private final CourseRepository courses;
    private final RegistrationRepository registrations; private final ExamRepository exams; private final GradeRepository grades;
    private final CoursePrerequisiteRepository prerequisites; private final EvaluationTypeRepository types;
    private final CourseSessionRepository sessions; private final AttendanceRepository attendances;
    private final CourseEvaluationRuleRepository rules;

    public DataInitializer(StudentRepository students, CourseRepository courses, RegistrationRepository registrations,
                           ExamRepository exams, GradeRepository grades, CoursePrerequisiteRepository prerequisites,
                           EvaluationTypeRepository types, CourseEvaluationRuleRepository rules, CourseSessionRepository sessions, AttendanceRepository attendances) {
        this.students=students;this.courses=courses;this.registrations=registrations;this.exams=exams;this.grades=grades;
        this.prerequisites=prerequisites;this.types=types;this.rules=rules;this.sessions=sessions;this.attendances=attendances;
    }

    @Override @Transactional
    public void run(String... args) {
        EvaluationType continuous=getOrCreateType("CONTINUOUS","Contrôle continu");
        EvaluationType exam=getOrCreateType("EXAM","Examen");
        EvaluationType project=getOrCreateType("PROJECT","Projet");
        if(students.count()==0) createDemoData(continuous,exam,project);
        // Les anciens examens sont classés comme examens lors de la migration vers les nouveaux types.
        for(Exam oldExam:exams.findAll()) {
            boolean changed=false;
            if(oldExam.evaluationType==null){oldExam.evaluationType=exam;changed=true;}
            if(oldExam.finalExam==null){oldExam.finalExam=false;changed=true;}
            if(oldExam.externalSyncStatus==null){oldExam.externalSyncStatus=com.gestetud.external.ExternalSyncStatus.NOT_SYNCED;changed=true;}
            if(changed)exams.save(oldExam);
        }
        var target=courses.findByCode("INF202"); var required=courses.findByCode("INF101");
        if(target.isPresent()&&required.isPresent()&&prerequisites.findByCourseIdAndPrerequisiteCourseId(target.get().id,required.get().id).isEmpty())
            prerequisites.save(new CoursePrerequisite(target.get(),required.get(),null,10.0));
        for(Course course:courses.findAll()) if(rules.findByCourseId(course.id).isEmpty()){
            rules.save(new CourseEvaluationRule(course,continuous,30.0));
            rules.save(new CourseEvaluationRule(course,exam,50.0));
            rules.save(new CourseEvaluationRule(course,project,20.0));
        }
    }

    private EvaluationType getOrCreateType(String code,String label){return types.findByCode(code).orElseGet(()->types.save(new EvaluationType(code,label)));}

    private void createDemoData(EvaluationType continuous,EvaluationType examType,EvaluationType project) {
        Student amina=students.save(new Student("Amina Benali","amina.benali@example.com","Informatique"));
        Student youssef=students.save(new Student("Youssef Amrani","youssef.amrani@example.com","Informatique"));
        Student sara=students.save(new Student("Sara El Fassi","sara.elfassi@example.com","Mathématiques"));
        Course java=courses.save(new Course("INF101","Programmation Java",30));
        Course db=courses.save(new Course("INF202","Bases de données",25));
        Course maths=courses.save(new Course("MAT110","Mathématiques discrètes",20));
        registrations.save(new Registration(amina,java));registrations.save(new Registration(youssef,java));
        registrations.save(new Registration(amina,db));registrations.save(new Registration(sara,maths));
        seedAttendance(java,amina,youssef,1,2);
        seedAttendance(db,amina,null,0,0);
        seedAttendance(maths,sara,null,0,0);

        Exam javaCc=exams.save(new Exam(java,LocalDate.of(2026,10,15),"Contrôle continu Java",continuous));
        Exam javaExam=exams.save(new Exam(java,LocalDate.of(2026,11,10),"Examen Java - session 1",examType,true));
        Exam javaProject=exams.save(new Exam(java,LocalDate.of(2026,11,5),"Projet Java",project));
        Exam dbCc=exams.save(new Exam(db,LocalDate.of(2026,10,20),"Contrôle continu bases de données",continuous));
        Exam dbExam=exams.save(new Exam(db,LocalDate.of(2026,11,11),"Examen bases de données",examType,true));
        Exam dbProject=exams.save(new Exam(db,LocalDate.of(2026,11,6),"Projet bases de données",project));
        Exam mathsCc=exams.save(new Exam(maths,LocalDate.of(2026,10,18),"Contrôle continu mathématiques",continuous));
        Exam mathsExam=exams.save(new Exam(maths,LocalDate.of(2026,11,12),"Examen mathématiques",examType,true));
        Exam mathsProject=exams.save(new Exam(maths,LocalDate.of(2026,11,7),"Projet mathématiques",project));
        grades.save(new Grade(javaCc,amina,15.0));grades.save(new Grade(javaExam,amina,16.5));grades.save(new Grade(javaProject,amina,18.0));
        grades.save(new Grade(javaCc,youssef,12.0));grades.save(new Grade(javaProject,youssef,14.0));
        grades.save(new Grade(dbCc,amina,17.0));grades.save(new Grade(dbExam,amina,18.0));grades.save(new Grade(dbProject,amina,19.0));
        grades.save(new Grade(mathsCc,sara,15.0));grades.save(new Grade(mathsExam,sara,15.0));grades.save(new Grade(mathsProject,sara,15.0));
    }

    private void seedAttendance(Course course,Student first,Student second,int firstAbsences,int secondAbsences){
        for(int i=0;i<5;i++){
            CourseSession session=sessions.save(new CourseSession(course,LocalDate.of(2026,10,1+i*3),"Séance "+(i+1)));
            if(first!=null)attendances.save(new Attendance(session,first,i<firstAbsences?AttendanceStatus.ABSENT:AttendanceStatus.PRESENT));
            if(second!=null)attendances.save(new Attendance(session,second,i<secondAbsences?AttendanceStatus.ABSENT:AttendanceStatus.PRESENT));
        }
    }
}
