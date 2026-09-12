
public abstract class Trajet {
    private int numero;
    private String villeDepart;
    private String villeArrivee;
    private String date;
    private String heureDepart;
    private int placesDisponibles;


    private int heuresAvantDepart;

    public Trajet(int numero, String villeDepart, String villeArrivee, String date,
                   String heureDepart, int placesDisponibles, int heuresAvantDepart) {
        this.numero = numero;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.date = date;
        this.heureDepart = heureDepart;
        this.placesDisponibles = placesDisponibles;
        this.heuresAvantDepart = heuresAvantDepart;
    }


    public boolean reserverPlace() {
        if (placesDisponibles <= 0) {
            System.out.println("Reservation refusee : le trajet #" + numero + " est complet.");
            return false;
        }
        placesDisponibles = placesDisponibles - 1;
        return true;
    }

    public void libererPlace() {
        placesDisponibles = placesDisponibles + 1;
    }

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
