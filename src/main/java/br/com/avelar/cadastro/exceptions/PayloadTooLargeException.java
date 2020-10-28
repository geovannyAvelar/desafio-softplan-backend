package br.com.avelar.cadastro.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
public class PayloadTooLargeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PayloadTooLargeException(String exception) {
        super(exception);
    }

}
