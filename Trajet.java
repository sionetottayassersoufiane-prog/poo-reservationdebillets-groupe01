public abstract class Trajet implements Reservable {
    private final int numero;
    private final String villeDepart;
    private final String villeArrivee;
    private final String date;
    private final String heureDepart;
    private int placesDisponibles;
    private final int heuresAvantDepart;
    public Trajet(int numero, String villeDepart, String villeArrivee, String date,
                   String heureDepart, int placesDisponibles, int heuresAvantDepart) {
        if (placesDisponibles < 0) {
            throw new IllegalArgumentException("Le nombre de places disponibles ne peut pas etre negatif.");
        }
        this.numero = numero;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.date = date;
        this.heureDepart = heureDepart;
        this.placesDisponibles = placesDisponibles;
        this.heuresAvantDepart = heuresAvantDepart;
    }

    @Override
    public void reserverPlace() throws TrajetCompletException {
        if (placesDisponibles <= 0) {
            throw new TrajetCompletException("Reservation refusee : le trajet " + numero + " est complet.");
        }
        placesDisponibles = placesDisponibles - 1;
    }

    @Override
    public void libererPlace() {
        placesDisponibles = placesDisponibles + 1;
    }

    @Override
    public boolean estComplet() {
        return placesDisponibles == 0;
    }
    public abstract int delaiAnnulationHeures();
    public void afficher() {
        System.out.println("Trajet " + numero + " : " + villeDepart + " -> " + villeArrivee
                + " le " + date + " a " + heureDepart + " (" + placesDisponibles
                + " place(s) disponible(s), depart dans " + heuresAvantDepart + "h)");
    }

    public int getNumero() {
        return numero;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public int getHeuresAvantDepart() {
        return heuresAvantDepart;
    }
}
