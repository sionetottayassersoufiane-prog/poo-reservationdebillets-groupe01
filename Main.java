package billetterie.ui;

import billetterie.exceptions.AnnulationImpossibleException;
import billetterie.exceptions.BilletterieException;
import billetterie.exceptions.DoublonException;
import billetterie.modele.StatutTicket;
import billetterie.modele.Ticket;
import billetterie.modele.Trajet;
import billetterie.modele.TrajetBus;
import billetterie.modele.TrajetTrain;
import billetterie.modele.TrajetVol;
import billetterie.modele.Voyageur;
import billetterie.service.Agence;
import billetterie.service.ComparateurVoyageurParNom;
import billetterie.service.Sauvegarde;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Agence agence = new Agence("Agence de voyage 2iE");
        boolean modeMenu = args.length > 0 && args[0].equals("menu");
        try {
            preparerAgence(agence);
            if (modeMenu) {
                new MenuConsole(agence, System.in, System.out).lancer();
                return;
            }
            demonstrationRecherche(agence);
            demonstrationBusEtVol(agence);
            demonstrationTrain(agence);
            demonstrationErreurs(agence);
            demonstrationFileAttente(agence);
            demonstrationBilan(agence);
            demonstrationSauvegarde(agence);
        } catch (BilletterieException e) {
            System.out.println("Erreur inattendue : " + e.getMessage());
        } finally {
            if (!modeMenu) {
                System.out.println("\nFin de la demonstration.");
            }
        }
    }

    private static void titre(String texte) {
        System.out.println("\n=== " + texte + " ===");
    }

    private static void preparerAgence(Agence agence) throws BilletterieException {
        titre("Enregistrement des voyageurs et des trajets");
        agence.enregistrerVoyageur(new Voyageur(1, "SIONE Totta Yasser Soufiane", "sioneyasser@gmail.com"));
        agence.enregistrerVoyageur(new Voyageur(2, "YELKUNI Tindwende Franck Onel", "yelkunifranck@gmail.com"));
        agence.enregistrerVoyageur(new Voyageur(3, "NIKIEMA P. Hanifah", "nikiemahanifah@gmail.com"));
        agence.enregistrerVoyageur(new Voyageur(4, "NADEMBEGA Ingrid Oceane", "nadembegaingrid@gmail.com"));

        agence.ajouterTrajet(new TrajetBus(101, "Ouagadougou", "Bobo-Dioulasso", "2026-10-20", "08:00", 2, 120, "STAF"));
        agence.ajouterTrajet(new TrajetVol(102, "Ouagadougou", "Abidjan", "2026-10-09", "07:00", 1, 48, "AH 512"));
        agence.ajouterTrajet(new TrajetBus(103, "Ouagadougou", "Ouahigouya", "2026-10-21", "10:00", 0, 96, "STAF"));
        agence.ajouterTrajet(new TrajetBus(104, "Ouagadougou", "Fada N'Gourma", "2026-11-01", "06:00", 3, 120, "STAF"));
        agence.ajouterTrajet(new TrajetVol(105, "Ouagadougou", "Marseille", "2026-11-10", "22:00", 3, 100, "AF 900"));
        agence.ajouterTrajet(new TrajetTrain(106, "Ouagadougou", "Banfora", "2026-11-15", "05:30", 2, 200, "SIT 12"));
        agence.ajouterTrajet(new TrajetTrain(107, "Ouagadougou", "Banfora", "2026-10-03", "06:00", 2, 36, "SIT 14"));
        agence.afficher();

        try {
            agence.enregistrerVoyageur(new Voyageur(5, "Doublon Email", "sioneyasser@gmail.com"));
        } catch (DoublonException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
        try {
            agence.ajouterTrajet(new TrajetBus(101, "Ouagadougou", "Koudougou", "2026-10-01", "09:00", 5, 96, "TSR"));
        } catch (DoublonException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
    }

    private static void demonstrationRecherche(Agence agence) {
        titre("Trajets disponibles, du plus proche au plus lointain");
        for (Trajet trajet : agence.trajetsDisponibles()) {
            trajet.afficher();
        }

        titre("Recherche Ouagadougou vers Bobo-Dioulasso");
        for (Trajet trajet : agence.rechercherTrajets("Ouagadougou", "Bobo-Dioulasso")) {
            trajet.afficher();
        }
    }

    private static void demonstrationBusEtVol(Agence agence) throws BilletterieException {
        titre("Reservation sur le bus 101");
        Ticket ticketBus = reserverEtAfficher(agence, 1, 101, 5000);

        titre("Annulation acceptee (bus, depart dans plus de 24h)");
        annulerEtAfficher(agence, ticketBus.getNumero());

        titre("Reservation sur le vol 102");
        Ticket ticketVol = reserverEtAfficher(agence, 2, 102, 95000);

        titre("Annulation refusee (vol, depart dans moins de 72h)");
        annulerEtAfficher(agence, ticketVol.getNumero());

        titre("Validation automatique au depart du vol 102");
        int valides = agence.validerDepart(102);
        System.out.println(valides + " ticket(s) valide(s).");
        ticketVol.afficher();
    }

    private static void demonstrationTrain(Agence agence) throws BilletterieException {
        titre("Train 106 : annulation acceptee (depart dans plus de 48h)");
        Ticket ticketAccepte = reserverEtAfficher(agence, 3, 106, 12000);
        annulerEtAfficher(agence, ticketAccepte.getNumero());

        titre("Train 107 : annulation refusee (depart dans moins de 48h)");
        Ticket ticketRefuse = reserverEtAfficher(agence, 4, 107, 12000);
        annulerEtAfficher(agence, ticketRefuse.getNumero());
    }

    private static void demonstrationErreurs(Agence agence) {
        titre("Erreurs metier gerees par les exceptions");
        try {
            agence.reserver(2, 102, 95000);
        } catch (BilletterieException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
        try {
            agence.reserver(99, 101, 5000);
        } catch (BilletterieException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
        try {
            agence.reserver(1, 101, -100);
        } catch (BilletterieException e) {
            System.out.println("Refus attendu : " + e.getMessage());
        }
    }

    private static void demonstrationFileAttente(Agence agence) throws BilletterieException {
        titre("Trajet 103 deja complet : le voyageur entre en file d'attente");
        reserverEtAfficher(agence, 1, 103, 4500);

        titre("Bus 104 avec 3 places pour 4 voyageurs");
        Ticket t1 = reserverEtAfficher(agence, 1, 104, 3500);
        reserverEtAfficher(agence, 2, 104, 3500);
        reserverEtAfficher(agence, 3, 104, 3500);
        Ticket t4 = reserverEtAfficher(agence, 4, 104, 3500);
        agence.chercherTrajet(104).afficher();

        titre("Une annulation libere une place pour le premier de la file");
        annulerEtAfficher(agence, t1.getNumero());
        t4.afficher();
        agence.chercherTrajet(104).afficher();

        titre("Vol 105 avec 3 places pour 4 voyageurs");
        reserverEtAfficher(agence, 1, 105, 275000);
        reserverEtAfficher(agence, 2, 105, 275000);
        reserverEtAfficher(agence, 3, 105, 275000);
        Ticket enAttente = reserverEtAfficher(agence, 4, 105, 275000);

        titre("Annulation d'un ticket encore en file d'attente");
        annulerEtAfficher(agence, enAttente.getNumero());
        System.out.println("Taille de la file du trajet 105 : " + agence.tailleFileAttente(105));
    }

    private static void demonstrationBilan(Agence agence) throws BilletterieException {
        titre("Voyageurs presents a la fois sur les trajets 104 et 105");
        Set<Voyageur> communs = agence.voyageursCommuns(104, 105);
        List<Voyageur> liste = new ArrayList<>(communs);
        Collections.sort(liste);
        for (Voyageur voyageur : liste) {
            voyageur.afficher();
        }

        titre("Voyageurs classes par ordre alphabetique");
        for (Voyageur voyageur : agence.voyageursTries(new ComparateurVoyageurParNom())) {
            voyageur.afficher();
        }

        titre("Tickets du voyageur 2");
        for (Ticket ticket : agence.ticketsDuVoyageur(2)) {
            ticket.afficher();
        }

        titre("Bilan de l'agence");
        Map<StatutTicket, Integer> compteurs = agence.compterParStatut();
        for (Map.Entry<StatutTicket, Integer> entree : compteurs.entrySet()) {
            System.out.println(entree.getKey() + " : " + entree.getValue());
        }
        System.out.println("Villes desservies : " + agence.villesDesservies());
        System.out.println("Chiffre d'affaires (tickets payes) : "
                + Ticket.formaterPrix(agence.chiffreAffaires()) + " FCFA");
        agence.afficher();
    }

    private static void demonstrationSauvegarde(Agence agence) {
        titre("Sauvegarde et rechargement de l'agence");
        Path fichier = null;
        try {
            fichier = Files.createTempFile("agence-2ie", ".bin");
            Sauvegarde.sauvegarder(agence, fichier.toString());
            Agence rechargee = Sauvegarde.charger(fichier.toString());
            rechargee.afficher();
            System.out.println("Meme nombre de tickets apres rechargement : "
                    + (rechargee.nombreTickets() == agence.nombreTickets()));
            System.out.println("Meme chiffre d'affaires apres rechargement : "
                    + (rechargee.chiffreAffaires() == agence.chiffreAffaires()));
        } catch (BilletterieException | IOException e) {
            System.out.println("Sauvegarde impossible : " + e.getMessage());
        } finally {
            supprimer(fichier);
        }
    }

    private static void supprimer(Path fichier) {
        if (fichier == null) {
            return;
        }
        try {
            Files.deleteIfExists(fichier);
        } catch (IOException e) {
            System.out.println("Fichier temporaire non supprime : " + fichier);
        }
    }

    private static Ticket reserverEtAfficher(Agence agence, int numeroVoyageur, int numeroTrajet, double prix)
            throws BilletterieException {
        Ticket ticket = agence.reserver(numeroVoyageur, numeroTrajet, prix);
        ticket.afficher();
        if (ticket.getStatut() == StatutTicket.EN_ATTENTE) {
            System.out.println("Le trajet " + numeroTrajet + " est complet : le ticket " + ticket.getNumero()
                    + " est place en file d'attente (position "
                    + agence.positionDansFile(ticket.getNumero()) + ").");
        }
        return ticket;
    }

    private static void annulerEtAfficher(Agence agence, int numeroTicket) throws BilletterieException {
        boolean enAttente = agence.chercherTicket(numeroTicket).getStatut() == StatutTicket.EN_ATTENTE;
        try {
            List<Ticket> promus = agence.annuler(numeroTicket);
            if (enAttente) {
                System.out.println("Ticket " + numeroTicket + " retire de la file d'attente.");
            } else {
                System.out.println("Ticket " + numeroTicket + " annule. Remboursement declenche.");
            }
            for (Ticket promu : promus) {
                System.out.println("Le ticket " + promu.getNumero()
                        + " quitte la file d'attente : une place lui est attribuee.");
            }
        } catch (AnnulationImpossibleException e) {
            System.out.println(e.getMessage());
        }
        agence.chercherTicket(numeroTicket).afficher();
    }
}
