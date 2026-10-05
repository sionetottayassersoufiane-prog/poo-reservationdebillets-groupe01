package billetterie.exceptions;

public class SauvegardeException extends BilletterieException {
    private static final long serialVersionUID = 1L;

    public SauvegardeException(String message) {
        super(message);
    }
}
