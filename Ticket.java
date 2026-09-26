import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
public class Ticket {
    private static final NumberFormat FORMAT_PRIX = creerFormatPrix();

    private static NumberFormat creerFormatPrix() {
        DecimalFormatSymbols symboles = new DecimalFormatSymbols(Locale.FRANCE);
        symboles.setGroupingSeparator(' ');
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,##0", symboles);
        return format;
    }

    private final int numero;
    private final Voyageur voyageur;
    private final Trajet trajet;
    private final double prix;
    private StatutTicket statut;
    public Ticket(int numero, Voyageur voyageur, Trajet trajet, double prix) {
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix d'un ticket ne peut pas etre negatif.");
        }
        this.numero = numero;
        this.voyageur = voyageur;
        this.trajet = trajet;
        this.prix = prix;

        try {
            trajet.reserverPlace();
            this.statut = StatutTicket.RESERVE;
        } catch (TrajetCompletException e) {
            this.statut = StatutTicket.REFUSE;
            System.out.println(e.getMessage());
        }
    }

    public boolean peutEtreAnnule() {
        return trajet.getHeuresAvantDepart() > trajet.delaiAnnulationHeures();
    }
    public void annuler() throws AnnulationImpossibleException {
        if (statut != StatutTicket.RESERVE) {
            throw new AnnulationImpossibleException("Annulation refusee : le ticket #" + numero
                    + " n'est pas au statut Reserve (statut actuel : " + statut + ").");
        }
        if (!peutEtreAnnule()) {
            throw new AnnulationImpossibleException("Annulation refusee : le delai d'annulation de ce trajet ("
                    + trajet.delaiAnnulationHeures() + "h avant depart) est depasse.");
        }
        statut = StatutTicket.ANNULE;
        trajet.libererPlace();
        System.out.println("Ticket #" + numero + " annule. Remboursement declenche.");
    }

    public void validerAuto() {
        if (statut == StatutTicket.RESERVE) {
            statut = StatutTicket.PAYE;
        }
    }

    public boolean estDejaReserve() {
        return statut == StatutTicket.RESERVE || statut == StatutTicket.PAYE;
    }

    public void afficher() {
        System.out.println("Ticket " + numero + " - " + voyageur.getNom()
                + " - trajet " + trajet.getNumero() + " (" + trajet.getVilleDepart()
                + " -> " + trajet.getVilleArrivee() + ") - " + FORMAT_PRIX.format(prix)
                + " FCFA - statut : " + statut);
    }

    public int getNumero() {
        return numero;
    }

    public StatutTicket getStatut() {
        return statut;
    }
}
