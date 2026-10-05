package billetterie.modele;

import billetterie.contrats.Affichable;
import billetterie.exceptions.AnnulationImpossibleException;
import billetterie.exceptions.TrajetCompletException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class Ticket implements Affichable, Serializable {
    private static final long serialVersionUID = 1L;
    private final int numero;
    private final Voyageur voyageur;
    private final Trajet trajet;
    private final double prix;
    private StatutTicket statut;

    public Ticket(int numero, Voyageur voyageur, Trajet trajet, double prix) {
        if (voyageur == null || trajet == null) {
            throw new IllegalArgumentException("Un ticket doit etre lie a un voyageur et a un trajet.");
        }
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
        }
    }

    public static String formaterPrix(double prix) {
        DecimalFormatSymbols symboles = new DecimalFormatSymbols(Locale.FRANCE);
        symboles.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("#,##0", symboles);
        return format.format(prix);
    }

    public boolean peutEtreAnnule() {
        return trajet.getHeuresAvantDepart() > trajet.delaiAnnulationHeures();
    }

    public void annuler() throws AnnulationImpossibleException {
        if (statut == StatutTicket.EN_ATTENTE) {
            statut = StatutTicket.ANNULE;
            return;
        }
        if (statut != StatutTicket.RESERVE) {
            throw new AnnulationImpossibleException("Annulation refusee : le ticket " + numero
                    + " n'est pas au statut Reserve (statut actuel : " + statut + ").");
        }
        if (!peutEtreAnnule()) {
            throw new AnnulationImpossibleException("Annulation refusee : le delai d'annulation de ce trajet ("
                    + trajet.delaiAnnulationHeures() + "h avant depart) est depasse.");
        }
        statut = StatutTicket.ANNULE;
        trajet.libererPlace();
    }

    public void validerAuto() {
        if (statut == StatutTicket.RESERVE) {
            statut = StatutTicket.PAYE;
        }
    }

    public void mettreEnAttente() {
        if (statut == StatutTicket.REFUSE) {
            statut = StatutTicket.EN_ATTENTE;
        }
    }

    public boolean reessayerReservation() {
        if (statut != StatutTicket.EN_ATTENTE) {
            return false;
        }
        try {
            trajet.reserverPlace();
            statut = StatutTicket.RESERVE;
            return true;
        } catch (TrajetCompletException e) {
            return false;
        }
    }

    public boolean estDejaReserve() {
        return statut == StatutTicket.RESERVE || statut == StatutTicket.PAYE;
    }

    public boolean estActif() {
        return estDejaReserve() || statut == StatutTicket.EN_ATTENTE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;
        Ticket autre = (Ticket) o;
        return numero == autre.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return "Ticket " + numero + " - " + voyageur.getNom()
                + " - trajet " + trajet.getNumero() + " (" + trajet.getVilleDepart()
                + " -> " + trajet.getVilleArrivee() + ") - " + formaterPrix(prix)
                + " FCFA - statut : " + statut;
    }

    @Override
    public void afficher() {
        System.out.println(this);
    }

    public int getNumero() {
        return numero;
    }

    public Voyageur getVoyageur() {
        return voyageur;
    }

    public Trajet getTrajet() {
        return trajet;
    }

    public double getPrix() {
        return prix;
    }

    public StatutTicket getStatut() {
        return statut;
    }
}
