package billetterie.modele;

public class TrajetBus extends Trajet {
    private static final long serialVersionUID = 1L;
    private final String compagnie;

    public TrajetBus(int numero, String villeDepart, String villeArrivee, String date,
                     String heureDepart, int placesDisponibles, int heuresAvantDepart,
                     String compagnie) {
        super(numero, villeDepart, villeArrivee, date, heureDepart, placesDisponibles, heuresAvantDepart);
        if (compagnie == null || compagnie.isBlank()) {
            throw new IllegalArgumentException("La compagnie du bus est obligatoire.");
        }
        this.compagnie = compagnie;
    }

    @Override
    public int delaiAnnulationHeures() {
        return 24;
    }

    @Override
    public void afficher() {
        super.afficher();
        System.out.println("   -> Bus, compagnie " + compagnie + " (annulation possible jusqu'a 24h avant depart)");
    }

    public String getCompagnie() {
        return compagnie;
    }
}
