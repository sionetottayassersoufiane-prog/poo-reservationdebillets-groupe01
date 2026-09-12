public class Ticket {
    private int numero;
    private Voyageur voyageur;
    private Trajet trajet;
    private double prix;
    private String statut;

    public Ticket(int numero, Voyageur voyageur, Trajet trajet, double prix) {
        this.numero = numero;
        this.voyageur = voyageur;
        this.trajet = trajet;
        this.prix = prix;

        trajet.reserverPlace();
        this.statut = "Reserve";
    }


    public boolean peutEtreAnnule() {
        return trajet.getHeuresAvantDepart() > trajet.delaiAnnulationHeures();
    }

    public void annuler() {
        if (!statut.equals("Reserve")) {
            System.out.println("Annulation refusee : le ticket #" + numero
                    + " n'est pas au statut Reserve (statut actuel : " + statut + ").");
            return;
        }
        if (!peutEtreAnnule()) {
            System.out.println("Annulation refusee : le delai d'annulation de ce trajet ("
                    + trajet.delaiAnnulationHeures() + "h avant depart) est depasse.");
            return;
        }
        statut = "Annule";
        trajet.libererPlace();
        System.out.println("Ticket #" + numero + " annule. Remboursement declenche.");
    }

    public void validerAuto() {
        if (statut.equals("Reserve")) {
            statut = "Paye";
        }
    }
    
    public boolean estDejaReserve() {
        return statut.equals("Reserve") || statut.equals("Paye");
    }

    public void afficher() {
        System.out.println("Ticket " + numero + " - " + voyageur.getNom()
                + " - trajet " + trajet.getNumero() + " (" + trajet.getVilleDepart()
                + " -> " + trajet.getVilleArrivee() + ") - " + prix + " FCFA - statut : " + statut);
    }

    public int getNumero() {
        return numero;
    }

    public String getStatut() {
        return statut;
    }
}
