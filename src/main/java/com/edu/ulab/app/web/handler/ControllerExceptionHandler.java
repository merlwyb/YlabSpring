package com.edu.ulab.app.web.handler;

import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NoSuchEntityException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.web.response.BaseWebResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseWebResponse> handleNotFoundExceptionException(@NonNull final NotFoundException exc) {
        log.error(exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(exc)));
    }

    @ExceptionHandler(EmptyFieldException.class)
    public ResponseEntity<BaseWebResponse> handleEmptyFieldException(@NonNull final EmptyFieldException exc) {
        log.error(exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(exc)));
    }

    @ExceptionHandler(NoSuchEntityException.class)
    public ResponseEntity<BaseWebResponse> handleNoSuchEntityException(@NonNull final NoSuchEntityException exc) {
        log.error(exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(exc)));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<BaseWebResponse> handleEmptyResultDataAccessException(@NonNull final EmptyResultDataAccessException exc) {
        log.error("An error occurred while trying to access the database: " + exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse("An error occurred while trying to access the database: " +
                        createErrorMessage(exc)));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BaseWebResponse> handleNullPointerException(@NonNull final NullPointerException exc) {
        log.error("NullPointerException occurred: " + exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse("NullPointerException occurred: " + createErrorMessage(exc)));
    }

    private String createErrorMessage(Exception exception) {
        final String message = exception.getMessage();
        log.error(ExceptionHandlerUtils.buildErrorMessage(exception));
        return message;
    }
}
