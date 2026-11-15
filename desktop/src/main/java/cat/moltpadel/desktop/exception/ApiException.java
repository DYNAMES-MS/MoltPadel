package cat.moltpadel.desktop.exception;

public class ApiException extends RuntimeException {
    public ApiException(String missatge) {
        super(missatge);
    }
}
