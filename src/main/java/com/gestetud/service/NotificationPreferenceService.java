package com.gestetud.service;

import com.gestetud.api.dto.NotificationPreferenceInput;
import com.gestetud.model.*;
import com.gestetud.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificationPreferenceService {
    private final StudentRepository students; private final NotificationPreferenceRepository preferences;
    public NotificationPreferenceService(StudentRepository students,NotificationPreferenceRepository preferences){this.students=students;this.preferences=preferences;}
    @Transactional(readOnly=true)
    public NotificationPreference find(Long studentId){
        Student student=students.findById(studentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Étudiant introuvable"));
        return preferences.findByStudentId(studentId).orElseGet(()->new NotificationPreference(student));
    }
    @Transactional
    public NotificationPreference update(Long studentId,NotificationPreferenceInput input){
        Student student=students.findById(studentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Étudiant introuvable"));
        NotificationPreference pref=preferences.findByStudentId(studentId).orElseGet(()->new NotificationPreference(student));
        pref.emailEnabled=input.emailEnabled(); pref.smsEnabled=input.smsEnabled(); pref.whatsappEnabled=input.whatsappEnabled(); pref.phoneNumber=input.phoneNumber();
        if((pref.smsEnabled||pref.whatsappEnabled)&&(pref.phoneNumber==null||pref.phoneNumber.isBlank()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Un numéro de téléphone est requis pour SMS ou WhatsApp");
        return preferences.save(pref);
    }
}
