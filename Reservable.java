
public interface Reservable {
    void reserverPlace() throws TrajetCompletException;
    void libererPlace();
    boolean estComplet();
}
