package pl.miquido.java.recruitmenttask.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import pl.miquido.java.recruitmenttask.model.ApiError;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ApiError> handleHttpStatusCodeExceptions(HttpStatusCodeException e) {
        return new ResponseEntity<>(
                new ApiError(e), e.getStatusCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(IllegalArgumentException e) {
        return new ResponseEntity<>(
                new ApiError(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableExceptions(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(
                new ApiError(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InappropriateCharacterException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundExceptions(InappropriateCharacterException e) {
        return new ResponseEntity<>(
                new ApiError(e), HttpStatus.BAD_REQUEST);
    }
}

