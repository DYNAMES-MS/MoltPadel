package cat.moltpadel.backend.exception;

import cat.moltpadel.backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.DisabledException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoTrobatException.class)
    public ErrorResponse handlerNoTrobat(NoTrobatException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParametreIncorrecteException.class)
    public ErrorResponse handlerParametreIncorrecte(ParametreIncorrecteException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handlerCredencialsIncorrectes(BadCredentialsException ex) {
        return new ErrorResponse("Email o contrasenya incorrectes");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(DisabledException.class)
    public ErrorResponse handlerCompteInactiu(DisabledException ex) {
        return new ErrorResponse("El compte està inactiu");
    }
}
