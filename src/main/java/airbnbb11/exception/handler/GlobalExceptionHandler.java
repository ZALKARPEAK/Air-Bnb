package airbnbb11.exception.handler;

import airbnbb11.exception.*;
import com.google.firebase.FirebaseException;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String MESSAGE_KEY = "message";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidArguments(@NonNull MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;

    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> lockedException(@NonNull LockedException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE_KEY, exception.getMessage());
        return errors;

    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(@NonNull EntityNotFoundException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE_KEY, exception.getMessage());
        return errors;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleBadCredentials(@NonNull BadCredentialsException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE_KEY, exception.getMessage());
        return errors;
    }

    @ExceptionHandler(FirebaseException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleFirebaseException(@NonNull FirebaseException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE_KEY, exception.getMessage());
        return errors;
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.FOUND)
    public Map<String, String> handlerAlreadyExist(AlreadyExistsException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE_KEY, exception.getMessage());
        return errors;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerNotFoundException(BadRequestException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE_KEY, exception.getMessage());
        return errors;
    }
}
