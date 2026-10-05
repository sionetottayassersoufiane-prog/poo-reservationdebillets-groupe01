package billetterie.modele;

public class TrajetVol extends Trajet {
    private static final long serialVersionUID = 1L;
    private final String numeroVol;

    public TrajetVol(int numero, String villeDepart, String villeArrivee, String date,
                     String heureDepart, int placesDisponibles, int heuresAvantDepart,
                     String numeroVol) {
        super(numero, villeDepart, villeArrivee, date, heureDepart, placesDisponibles, heuresAvantDepart);
        if (numeroVol == null || numeroVol.isBlank()) {
            throw new IllegalArgumentException("Le numero de vol est obligatoire.");
        }
        this.numeroVol = numeroVol;
    }

    @Override
    public int delaiAnnulationHeures() {
        return 72;
    }

    @Override
    public void afficher() {
        super.afficher();
        System.out.println("   -> Vol " + numeroVol + " (annulation possible jusqu'a 72h avant depart)");
    }

    public String getNumeroVol() {
        return numeroVol;
    }
}
