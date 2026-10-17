package cat.moltpadel.backend.exception;

public class ParametreIncorrecteException extends RuntimeException{
    public ParametreIncorrecteException(String missatge) {
        super(missatge);
    }
}
