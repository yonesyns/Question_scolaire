package com.gestetud.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

public abstract class ApiSupport {
    protected <T> T require(Optional<T> result, String what) {
        return result.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, what + " introuvable"));
    }
}
