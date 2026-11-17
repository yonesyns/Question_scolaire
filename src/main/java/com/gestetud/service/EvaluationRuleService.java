package com.gestetud.service;

import com.gestetud.api.dto.EvaluationRuleInput;
import com.gestetud.model.*;
import com.gestetud.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
public class EvaluationRuleService {
    private final CourseRepository courses; private final EvaluationTypeRepository types; private final CourseEvaluationRuleRepository rules;
    public EvaluationRuleService(CourseRepository courses,EvaluationTypeRepository types,CourseEvaluationRuleRepository rules){this.courses=courses;this.types=types;this.rules=rules;}
    @Transactional(readOnly=true)
    public List<CourseEvaluationRule> find(Long courseId){requireCourse(courseId);return rules.findByCourseId(courseId);}
    @Transactional
    public List<CourseEvaluationRule> replace(Long courseId,List<EvaluationRuleInput> inputs){
        Course course=requireCourse(courseId);
        if(inputs==null||inputs.isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Au moins une pondération est requise");
        Set<String> seen=new HashSet<>(); double sum=0;
        for(EvaluationRuleInput input:inputs){
            if(!seen.add(input.evaluationTypeCode())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Un type d'évaluation ne peut apparaître qu'une fois");
            if(input.weight()==null||input.weight()<0||input.weight()>100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chaque pondération doit être comprise entre 0 et 100");
            sum+=input.weight();
            if(types.findByCode(input.evaluationTypeCode()).isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Type d'évaluation inconnu : "+input.evaluationTypeCode());
        }
        if(Math.abs(sum-100.0)>0.0001)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La somme des pondérations doit être égale à 100 (actuellement "+sum+")");
        rules.deleteByCourseId(course.id);
        List<CourseEvaluationRule> saved=new ArrayList<>();
        for(EvaluationRuleInput input:inputs){
            EvaluationType type=types.findByCode(input.evaluationTypeCode()).orElseThrow();
            saved.add(rules.save(new CourseEvaluationRule(course,type,input.weight())));
        }
        return saved;
    }
    private Course requireCourse(Long id){return courses.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Module introuvable"));}
}
