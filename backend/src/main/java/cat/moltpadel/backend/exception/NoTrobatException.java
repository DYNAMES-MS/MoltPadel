package cat.moltpadel.backend.exception;

public class NoTrobatException extends RuntimeException{
    public NoTrobatException(String missatge){
        super(missatge);
    }
}
