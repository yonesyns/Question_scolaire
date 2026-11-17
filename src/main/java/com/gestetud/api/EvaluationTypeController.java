package com.gestetud.api;

import com.gestetud.api.dto.EvaluationTypeInput;
import com.gestetud.model.EvaluationType;
import com.gestetud.repo.EvaluationTypeRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/evaluation-types")
public class EvaluationTypeController {
    private final EvaluationTypeRepository types;
    public EvaluationTypeController(EvaluationTypeRepository types) { this.types = types; }
    @GetMapping public List<EvaluationType> findAll() { return types.findAll(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public EvaluationType create(@Valid @RequestBody EvaluationTypeInput input) {
        if (types.findByCode(input.code()).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Ce code de type existe déjà");
        return types.save(new EvaluationType(input.code(),input.label()));
    }
    @PutMapping("/{id}")
    public EvaluationType update(@PathVariable Long id,@Valid @RequestBody EvaluationTypeInput input) {
        EvaluationType type=types.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Type d'évaluation introuvable"));
        types.findByCode(input.code()).filter(other->!other.id.equals(id)).ifPresent(other->{throw new ResponseStatusException(HttpStatus.CONFLICT,"Ce code de type existe déjà");});
        type.code=input.code(); type.label=input.label(); return types.save(type);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        EvaluationType type=types.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Type d'évaluation introuvable"));
        try { types.delete(type); types.flush(); }
        catch(DataIntegrityViolationException ex) { throw new ResponseStatusException(HttpStatus.CONFLICT,"Ce type est utilisé par des examens ou des règles de notation"); }
    }
}
