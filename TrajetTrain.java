package billetterie.modele;

public class TrajetTrain extends Trajet {
    private static final long serialVersionUID = 1L;

    private final String numeroTrain;

    public TrajetTrain(int numero, String villeDepart, String villeArrivee, String date,
                       String heureDepart, int placesDisponibles, int heuresAvantDepart,
                       String numeroTrain) {
        super(numero, villeDepart, villeArrivee, date, heureDepart, placesDisponibles, heuresAvantDepart);
        if (numeroTrain == null || numeroTrain.isBlank()) {
            throw new IllegalArgumentException("Le numero de train est obligatoire.");
        }
        this.numeroTrain = numeroTrain;
    }

    @Override
    public int delaiAnnulationHeures() {
        return 48;
    }

    @Override
    public void afficher() {
        super.afficher();
        System.out.println("   -> Train " + numeroTrain + " (annulation possible jusqu'a 48h avant depart)");
    }

    public String getNumeroTrain() {
        return numeroTrain;
    }
}
