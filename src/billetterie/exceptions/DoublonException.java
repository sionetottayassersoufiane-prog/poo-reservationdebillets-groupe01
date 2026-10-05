package billetterie.exceptions;

public class DoublonException extends BilletterieException {
    private static final long serialVersionUID = 1L;

    public DoublonException(String message) {
        super(message);
    }
}
