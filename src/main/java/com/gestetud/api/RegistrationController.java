package com.gestetud.api;

import com.gestetud.api.dto.RegistrationInput;
import com.gestetud.model.Registration;
import com.gestetud.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class RegistrationController {
    private final RegistrationService service;
    public RegistrationController(RegistrationService service) { this.service = service; }

    @PostMapping("/registrations") @ResponseStatus(HttpStatus.CREATED)
    public Registration register(@Valid @RequestBody RegistrationInput input) {
        return service.register(input.studentId(), input.courseId());
    }

    @GetMapping("/students/{id}/registrations")
    public List<Registration> findByStudent(@PathVariable Long id) { return service.findByStudent(id); }
}
