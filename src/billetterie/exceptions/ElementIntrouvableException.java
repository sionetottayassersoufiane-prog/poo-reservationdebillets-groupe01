package billetterie.exceptions;

public class ElementIntrouvableException extends BilletterieException {
    private static final long serialVersionUID = 1L;

    public ElementIntrouvableException(String message) {
        super(message);
    }
}
