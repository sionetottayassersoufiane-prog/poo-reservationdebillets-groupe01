package billetterie.ui;

import billetterie.exceptions.BilletterieException;
import billetterie.modele.StatutTicket;
import billetterie.modele.Ticket;
import billetterie.modele.Trajet;
import billetterie.modele.Voyageur;
import billetterie.service.Agence;
import billetterie.service.ComparateurVoyageurParNom;
import billetterie.service.Sauvegarde;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MenuConsole {
    private Agence agence;
    private final Scanner entree;
    private final PrintStream sortie;

    public MenuConsole(Agence agence, InputStream flux, PrintStream sortie) {
        if (agence == null || flux == null || sortie == null) {
            throw new IllegalArgumentException("L'agence, le flux d'entree et le flux de sortie sont obligatoires.");
        }
        this.agence = agence;
        this.entree = new Scanner(flux);
        this.sortie = sortie;
    }

    public Agence getAgence() {
        return agence;
    }

    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            try {
                afficherMenu();
                int choix = lireEntier("Votre choix : ");
                continuer = executer(choix);
            } catch (NoSuchElementException e) {
                sortie.println("Fin de la saisie.");
                continuer = false;
            } catch (IllegalArgumentException e) {
                sortie.println("Saisie invalide : " + e.getMessage());
            }
        }
        sortie.println("Au revoir.");
    }

    private void afficherMenu() {
        sortie.println();
        sortie.println("===== " + agence.getNom() + " =====");
        sortie.println("1  Lister les trajets disponibles");
        sortie.println("2  Rechercher un trajet par villes");
        sortie.println("3  Enregistrer un voyageur");
        sortie.println("4  Reserver une place");
        sortie.println("5  Annuler un ticket");
        sortie.println("6  Valider le depart d'un trajet");
        sortie.println("7  Voir les tickets d'un voyageur");
        sortie.println("8  Afficher les statistiques");
        sortie.println("9  Sauvegarder l'agence dans un fichier");
        sortie.println("10 Charger une agence depuis un fichier");
        sortie.println("0  Quitter");
    }

    private boolean executer(int choix) {
        try {
            switch (choix) {
                case 0:
                    return false;
                case 1:
                    listerTrajets();
                    break;
                case 2:
                    rechercherTrajets();
                    break;
                case 3:
                    enregistrerVoyageur();
                    break;
                case 4:
                    reserver();
                    break;
                case 5:
                    annuler();
                    break;
                case 6:
                    validerDepart();
                    break;
                case 7:
                    afficherTicketsDuVoyageur();
                    break;
                case 8:
                    afficherStatistiques();
                    break;
                case 9:
                    sauvegarder();
                    break;
                case 10:
                    charger();
                    break;
                default:
                    sortie.println("Choix inconnu : " + choix);
            }
        } catch (BilletterieException e) {
            sortie.println("Erreur : " + e.getMessage());
        }
        return true;
    }

    private void listerTrajets() {
        List<Trajet> disponibles = agence.trajetsDisponibles();
        if (disponibles.isEmpty()) {
            sortie.println("Aucun trajet disponible.");
        }
        for (Trajet trajet : disponibles) {
            sortie.println(trajet);
        }
    }

    private void rechercherTrajets() {
        String depart = lireTexte("Ville de depart : ");
        String arrivee = lireTexte("Ville d'arrivee : ");
        List<Trajet> resultat = agence.rechercherTrajets(depart, arrivee);
        if (resultat.isEmpty()) {
            sortie.println("Aucun trajet trouve.");
        }
        for (Trajet trajet : resultat) {
            sortie.println(trajet);
        }
    }

    private void enregistrerVoyageur() throws BilletterieException {
        int numero = lireEntier("Numero du voyageur : ");
        String nom = lireTexte("Nom : ");
        String email = lireTexte("Email : ");
        Voyageur voyageur = new Voyageur(numero, nom, email);
        agence.enregistrerVoyageur(voyageur);
        sortie.println("Voyageur enregistre : " + voyageur);
    }

    private void reserver() throws BilletterieException {
        int numeroVoyageur = lireEntier("Numero du voyageur : ");
        int numeroTrajet = lireEntier("Numero du trajet : ");
        double prix = lireDecimal("Prix en FCFA : ");
        Ticket ticket = agence.reserver(numeroVoyageur, numeroTrajet, prix);
        sortie.println(ticket);
        if (ticket.getStatut() == StatutTicket.EN_ATTENTE) {
            sortie.println("Le trajet est complet : position " + agence.positionDansFile(ticket.getNumero())
                    + " dans la file d'attente.");
        }
    }

    private void annuler() throws BilletterieException {
        int numeroTicket = lireEntier("Numero du ticket : ");
        List<Ticket> promus = agence.annuler(numeroTicket);
        sortie.println("Ticket " + numeroTicket + " annule.");
        for (Ticket promu : promus) {
            sortie.println("Le ticket " + promu.getNumero() + " obtient la place liberee.");
        }
    }

    private void validerDepart() throws BilletterieException {
        int numeroTrajet = lireEntier("Numero du trajet : ");
        int valides = agence.validerDepart(numeroTrajet);
        sortie.println(valides + " ticket(s) valide(s).");
    }

    private void afficherTicketsDuVoyageur() throws BilletterieException {
        int numeroVoyageur = lireEntier("Numero du voyageur : ");
        List<Ticket> tickets = agence.ticketsDuVoyageur(numeroVoyageur);
        if (tickets.isEmpty()) {
            sortie.println("Ce voyageur n'a aucun ticket.");
        }
        for (Ticket ticket : tickets) {
            sortie.println(ticket);
        }
    }

    private void afficherStatistiques() {
        sortie.println(agence.getNom() + " : " + agence.nombreVoyageurs() + " voyageur(s), "
                + agence.nombreTrajets() + " trajet(s), " + agence.nombreTickets() + " ticket(s).");
        for (Map.Entry<StatutTicket, Integer> ligne : agence.compterParStatut().entrySet()) {
            sortie.println(ligne.getKey() + " : " + ligne.getValue());
        }
        sortie.println("Villes desservies : " + agence.villesDesservies());
        sortie.println("Chiffre d'affaires : " + Ticket.formaterPrix(agence.chiffreAffaires()) + " FCFA");
        for (Voyageur voyageur : agence.voyageursTries(new ComparateurVoyageurParNom())) {
            sortie.println(voyageur);
        }
    }

    private void sauvegarder() throws BilletterieException {
        String chemin = lireTexte("Chemin du fichier : ");
        Sauvegarde.sauvegarder(agence, chemin);
        sortie.println("Agence sauvegardee dans " + chemin);
    }

    private void charger() throws BilletterieException {
        String chemin = lireTexte("Chemin du fichier : ");
        agence = Sauvegarde.charger(chemin);
        sortie.println("Agence chargee : " + agence.getNom());
    }

    private String lireTexte(String invite) {
        sortie.print(invite);
        return entree.nextLine().trim();
    }

    private int lireEntier(String invite) {
        String texte = lireTexte(invite);
        try {
            return Integer.parseInt(texte);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("il faut saisir un nombre entier, et non \"" + texte + "\".");
        }
    }

    private double lireDecimal(String invite) {
        String texte = lireTexte(invite);
        try {
            return Double.parseDouble(texte);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("il faut saisir un nombre, et non \"" + texte + "\".");
        }
    }
}
