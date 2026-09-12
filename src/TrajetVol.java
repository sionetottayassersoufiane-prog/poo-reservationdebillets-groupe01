public class TrajetVol extends Trajet {
    private String numeroVol;

    public TrajetVol(int numero, String villeDepart, String villeArrivee, String date,
                      String heureDepart, int placesDisponibles, int heuresAvantDepart,
                      String numeroVol) {
        super(numero, villeDepart, villeArrivee, date, heureDepart, placesDisponibles, heuresAvantDepart);
        this.numeroVol = numeroVol;
    }

    @Override
    public int delaiAnnulationHeures() {
        return 72;
    }

    @Override
    public void afficher() {
        super.afficher();
        System.out.println("    Vol " + numeroVol + " (annulation possible jusqu'a 72h avant depart)");
    }

    public String getNumeroVol() {
        return numeroVol;
    }
}
