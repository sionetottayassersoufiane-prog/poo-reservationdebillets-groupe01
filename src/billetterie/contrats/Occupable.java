package billetterie.contrats;

import billetterie.exceptions.TrajetCompletException;

public interface Occupable {
    void reserverPlace() throws TrajetCompletException;
    void libererPlace();
    boolean estComplet();
}
