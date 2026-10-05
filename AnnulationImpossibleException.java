package billetterie.exceptions;

public class AnnulationImpossibleException extends BilletterieException {
    private static final long serialVersionUID = 1L;

    public AnnulationImpossibleException(String message) {
        super(message);
    }
}
