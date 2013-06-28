package at.tuwien.prip.mozcore.exception;

public class KitException extends Exception {

    private static final long serialVersionUID = 3140896776782335722L;

    public KitException() {
        super();
    }

    public KitException(String message) {
        super(message);
    }

    public KitException(String message, Throwable cause) {
        super(message, cause);
    }

    public KitException(Throwable cause) {
        super(cause);
    }

}
