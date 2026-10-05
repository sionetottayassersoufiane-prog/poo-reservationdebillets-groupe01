package billetterie.exceptions;

public class TrajetCompletException extends BilletterieException {
    private static final long serialVersionUID = 1L;

    public TrajetCompletException(String message) {
        super(message);
    }
}
