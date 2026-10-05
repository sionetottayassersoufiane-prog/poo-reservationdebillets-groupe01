package billetterie.exceptions;

public class ReservationInvalideException extends BilletterieException {
    private static final long serialVersionUID = 1L;

    public ReservationInvalideException(String message) {
        super(message);
    }
}
