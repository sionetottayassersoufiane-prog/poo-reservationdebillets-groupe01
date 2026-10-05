package billetterie.tests;

import billetterie.contrats.Affichable;
import billetterie.contrats.Occupable;
import billetterie.exceptions.AnnulationImpossibleException;
import billetterie.exceptions.BilletterieException;
import billetterie.exceptions.DoublonException;
import billetterie.exceptions.ElementIntrouvableException;
import billetterie.exceptions.ReservationInvalideException;
import billetterie.exceptions.SauvegardeException;
import billetterie.exceptions.TrajetCompletException;
import billetterie.modele.StatutTicket;
import billetterie.modele.Ticket;
import billetterie.modele.Trajet;
import billetterie.modele.TrajetBus;
import billetterie.modele.TrajetTrain;
import billetterie.modele.TrajetVol;
import billetterie.modele.Voyageur;
import billetterie.service.Agence;
import billetterie.service.ComparateurVoyageurParNom;
import billetterie.service.Depot;
import billetterie.service.Sauvegarde;
import billetterie.ui.MenuConsole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestsScenarios {

    private static int reussites = 0;
    private static int echecs = 0;

    private interface Action {
        void executer() throws Exception;
    }

    public static void main(String[] args) {
        lancer(TestsScenarios::testInterfaces);
        lancer(TestsScenarios::testReservationAcceptee);
        lancer(TestsScenarios::testTrajetComplet);
        lancer(TestsScenarios::testAnnulationAcceptee);
        lancer(TestsScenarios::testAnnulationRefusee);
        lancer(TestsScenarios::testOperationsInterditesSansEffet);
        lancer(TestsScenarios::testFiabiliteQuatreVoyageurs);
        lancer(TestsScenarios::testCasLimitesEtValidation);
        lancer(TestsScenarios::testDepotGenerique);
        lancer(TestsScenarios::testEqualsEtHashCode);
        lancer(TestsScenarios::testTris);
        lancer(TestsScenarios::testAgenceEnregistrement);
        lancer(TestsScenarios::testAgenceReservation);
        lancer(TestsScenarios::testFileAttente);
        lancer(TestsScenarios::testValidationEtStatistiques);
        lancer(TestsScenarios::testEnsemblesEtRecherche);
        lancer(TestsScenarios::testAtomicite);
        lancer(TestsScenarios::testHierarchieExceptions);
        lancer(TestsScenarios::testValidationTrajets);
        lancer(TestsScenarios::testTrain);
        lancer(TestsScenarios::testSauvegarde);
        lancer(TestsScenarios::testMenu);

        System.out.println("\n=== Bilan : " + reussites + " test(s) reussi(s), " + echecs + " echec(s) ===");
        if (echecs > 0) {
            System.exit(1);
        }
    }

    private static void lancer(Action action) {
        try {
            action.executer();
        } catch (Exception e) {
            System.out.println("[ECHEC]  exception inattendue : " + e);
            echecs++;
        }
    }

    private static void verifier(String intitule, boolean condition) {
        if (condition) {
            System.out.println("[OK]     " + intitule);
            reussites++;
        } else {
            System.out.println("[ECHEC]  " + intitule);
            echecs++;
        }
    }

    private static void verifierException(String intitule, Class<? extends Exception> type, Action action) {
        try {
            action.executer();
            verifier(intitule, false);
        } catch (Exception e) {
            verifier(intitule, type.isInstance(e));
        }
    }

    private static void tenterAnnulation(Ticket ticket) {
        try {
            ticket.annuler();
        } catch (AnnulationImpossibleException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Agence creerAgenceDeTest() throws BilletterieException {
        Agence agence = new Agence("Agence de test");
        agence.enregistrerVoyageur(new Voyageur(1, "SIONE Yasser", "yasser@example.com"));
        agence.enregistrerVoyageur(new Voyageur(2, "YELKUNI Franck", "franck@example.com"));
        agence.enregistrerVoyageur(new Voyageur(3, "NIKIEMA Hanifah", "hanifah@example.com"));
        agence.enregistrerVoyageur(new Voyageur(4, "NADEMBEGA Ingrid", "ingrid@example.com"));
        agence.ajouterTrajet(new TrajetBus(10, "Ouagadougou", "Bobo-Dioulasso", "2026-10-20", "08:00", 2, 120, "STAF"));
        agence.ajouterTrajet(new TrajetVol(20, "Ouagadougou", "Abidjan", "2026-10-09", "07:00", 1, 48, "AH 512"));
        agence.ajouterTrajet(new TrajetBus(30, "Ouagadougou", "Koudougou", "2026-10-25", "09:00", 0, 96, "TSR"));
        return agence;
    }

    private static void testInterfaces() {
        System.out.println("\n-- Test 0 : utilisation polymorphe des interfaces Occupable et Affichable --");

        Occupable busOccupable = new TrajetBus(401, "Ouagadougou", "Banfora", "2026-12-01", "07:00", 1, 96, "STAF");
        Occupable volOccupable = new TrajetVol(402, "Ouagadougou", "Accra", "2026-12-02", "09:00", 1, 96, "AH 200");

        verifier("un TrajetBus vu comme Occupable n'est pas complet au depart", !busOccupable.estComplet());
        verifier("un TrajetVol vu comme Occupable n'est pas complet au depart", !volOccupable.estComplet());

        try {
            busOccupable.reserverPlace();
            verifier("reserverPlace() via Occupable fonctionne sur le bus", true);
        } catch (TrajetCompletException e) {
            verifier("reserverPlace() via Occupable fonctionne sur le bus", false);
        }
        verifier("le bus vu comme Occupable est complet apres la reservation", busOccupable.estComplet());

        try {
            busOccupable.reserverPlace();
            verifier("reserverPlace() via Occupable echoue si le trajet est deja complet", false);
        } catch (TrajetCompletException e) {
            verifier("reserverPlace() via Occupable echoue si le trajet est deja complet", true);
        }

        busOccupable.libererPlace();
        verifier("libererPlace() via Occupable rend la place disponible", !busOccupable.estComplet());

        Affichable[] elements = {
            new Voyageur(1, "SIONE Yasser", "yasser@example.com"),
            (Affichable) busOccupable,
            new Agence("Agence polymorphe")
        };
        int affiches = 0;
        for (Affichable element : elements) {
            element.afficher();
            affiches++;
        }
        verifier("les trois objets Affichable sont traites dans une meme boucle", affiches == 3);
    }

    private static void testReservationAcceptee() {
        System.out.println("\n-- Test 1 : reservation acceptee --");
        Voyageur v = new Voyageur(1, "SIONE Yasser", "yasser@example.com");
        Trajet t = new TrajetBus(201, "Ouagadougou", "Bobo-Dioulasso", "2026-10-01", "08:00", 2, 120, "STAF");

        int placesAvant = t.getPlacesDisponibles();
        Ticket ticket = new Ticket(9001, v, t, 5000);

        verifier("le ticket est cree avec le statut RESERVE", ticket.getStatut() == StatutTicket.RESERVE);
        verifier("une place a bien ete prise sur le trajet", t.getPlacesDisponibles() == placesAvant - 1);
    }

    private static void testTrajetComplet() {
        System.out.println("\n-- Test 2 : trajet complet --");
        Voyageur v = new Voyageur(2, "YELKUNI Franck", "franck@example.com");
        Trajet t = new TrajetVol(202, "Ouagadougou", "Paris", "2026-10-05", "23:00", 0, 100, "AF 713");

        verifier("le trajet est bien annonce comme complet", t.estComplet());

        int placesAvant = t.getPlacesDisponibles();
        Ticket ticket = new Ticket(9002, v, t, 250000);

        verifier("le ticket nait avec le statut REFUSE", ticket.getStatut() == StatutTicket.REFUSE);
        verifier("aucune place n'a ete retiree d'un trajet deja complet", t.getPlacesDisponibles() == placesAvant);
    }

    private static void testAnnulationAcceptee() {
        System.out.println("\n-- Test 3 : annulation acceptee --");
        Voyageur v = new Voyageur(3, "NIKIEMA Hanifah", "hanifah@example.com");
        Trajet t = new TrajetBus(203, "Ouagadougou", "Koudougou", "2026-10-10", "09:00", 3, 48, "TSR");
        Ticket ticket = new Ticket(9003, v, t, 3000);

        int placesAvant = t.getPlacesDisponibles();
        try {
            ticket.annuler();
            verifier("l'annulation ne leve pas d'exception quand le delai est respecte", true);
        } catch (AnnulationImpossibleException e) {
            verifier("l'annulation ne leve pas d'exception quand le delai est respecte", false);
        }

        verifier("le ticket passe au statut ANNULE", ticket.getStatut() == StatutTicket.ANNULE);
        verifier("la place annulee est bien reliberee", t.getPlacesDisponibles() == placesAvant + 1);
    }

    private static void testAnnulationRefusee() {
        System.out.println("\n-- Test 4 : annulation refusee --");
        Voyageur v = new Voyageur(4, "NADEMBEGA Ingrid", "ingrid@example.com");
        Trajet t = new TrajetVol(204, "Ouagadougou", "Dakar", "2026-10-02", "06:00", 2, 48, "HF 101");
        Ticket ticket = new Ticket(9004, v, t, 180000);

        StatutTicket statutAvant = ticket.getStatut();
        int placesAvant = t.getPlacesDisponibles();

        try {
            ticket.annuler();
            verifier("annuler() leve bien AnnulationImpossibleException hors delai", false);
        } catch (AnnulationImpossibleException e) {
            verifier("annuler() leve bien AnnulationImpossibleException hors delai", true);
        }

        verifier("le statut reste inchange quand le delai est depasse", ticket.getStatut() == statutAvant);
        verifier("aucune place n'est reliberee sur une annulation refusee", t.getPlacesDisponibles() == placesAvant);
    }

    private static void testOperationsInterditesSansEffet() {
        System.out.println("\n-- Test 5 : operations interdites sans effet sur l'etat --");
        Voyageur v = new Voyageur(5, "SIONE Yasser", "yasser2@example.com");
        Trajet t = new TrajetBus(205, "Ouagadougou", "Ouahigouya", "2026-10-15", "07:00", 2, 120, "STAF");
        Ticket ticket = new Ticket(9005, v, t, 4000);

        tenterAnnulation(ticket);
        StatutTicket statutApresAnnulation = ticket.getStatut();
        int placesApresAnnulation = t.getPlacesDisponibles();

        tenterAnnulation(ticket);
        verifier("annuler un ticket deja Annule ne change pas son statut",
                ticket.getStatut() == statutApresAnnulation);
        verifier("annuler un ticket deja Annule ne libere pas de place supplementaire",
                t.getPlacesDisponibles() == placesApresAnnulation);

        ticket.validerAuto();
        verifier("valider un ticket Annule ne le fait pas passer a Paye",
                ticket.getStatut() == statutApresAnnulation);

        Trajet complet = new TrajetVol(206, "Ouagadougou", "Lome", "2026-10-20", "12:00", 0, 96, "ET 900");
        Ticket ticketRefuse = new Ticket(9006, v, complet, 210000);
        int placesAvantOperations = complet.getPlacesDisponibles();

        tenterAnnulation(ticketRefuse);
        verifier("annuler un ticket Refuse ne change pas son statut",
                ticketRefuse.getStatut() == StatutTicket.REFUSE);
        verifier("annuler un ticket Refuse ne modifie pas les places du trajet",
                complet.getPlacesDisponibles() == placesAvantOperations);
    }

    private static void testFiabiliteQuatreVoyageurs() {
        System.out.println("\n-- Test 6 : fiabilite avec 4 voyageurs sur un bus et sur un vol --");

        Trajet bus = new TrajetBus(301, "Ouagadougou", "Fada N'Gourma", "2026-11-01", "06:00", 3, 120, "STAF");
        Voyageur busV1 = new Voyageur(10, "SIONE Totta Yasser Soufiane", "sioneyasser@gmail.com");
        Voyageur busV2 = new Voyageur(11, "YELKUNI Tindwende Franck Onel", "yelkunifranck@gmail.com");
        Voyageur busV3 = new Voyageur(12, "NIKIEMA P. Hanifah", "nikiemahanifah@gmail.com");
        Voyageur busV4 = new Voyageur(13, "NADEMBEGA Ingrid Oceane", "nadembegaingrid@gmail.com");

        Ticket ticketBus1 = new Ticket(9101, busV1, bus, 3500);
        Ticket ticketBus2 = new Ticket(9102, busV2, bus, 3500);
        Ticket ticketBus3 = new Ticket(9103, busV3, bus, 3500);
        Ticket ticketBus4 = new Ticket(9104, busV4, bus, 3500);

        verifier("les 3 premiers voyageurs du bus obtiennent bien le statut RESERVE",
                ticketBus1.getStatut() == StatutTicket.RESERVE
                        && ticketBus2.getStatut() == StatutTicket.RESERVE
                        && ticketBus3.getStatut() == StatutTicket.RESERVE);
        verifier("le 4e voyageur du bus est refuse car il n'y a plus de place",
                ticketBus4.getStatut() == StatutTicket.REFUSE);
        verifier("le bus n'a plus aucune place disponible apres les 3 reservations",
                bus.getPlacesDisponibles() == 0);

        Trajet vol = new TrajetVol(302, "Ouagadougou", "Marseille", "2026-11-10", "22:00", 3, 100, "AF 900");
        Voyageur volV1 = new Voyageur(20, "SIONE Totta Yasser Soufiane", "sioneyasser@gmail.com");
        Voyageur volV2 = new Voyageur(21, "YELKUNI Tindwende Franck Onel", "yelkunifranck@gmail.com");
        Voyageur volV3 = new Voyageur(22, "NIKIEMA P. Hanifah", "nikiemahanifah@gmail.com");
        Voyageur volV4 = new Voyageur(23, "NADEMBEGA Ingrid Oceane", "nadembegaingrid@gmail.com");

        Ticket ticketVol1 = new Ticket(9201, volV1, vol, 275000);
        Ticket ticketVol2 = new Ticket(9202, volV2, vol, 275000);
        Ticket ticketVol3 = new Ticket(9203, volV3, vol, 275000);
        Ticket ticketVol4 = new Ticket(9204, volV4, vol, 275000);

        verifier("les 3 premiers voyageurs du vol obtiennent bien le statut RESERVE",
                ticketVol1.getStatut() == StatutTicket.RESERVE
                        && ticketVol2.getStatut() == StatutTicket.RESERVE
                        && ticketVol3.getStatut() == StatutTicket.RESERVE);
        verifier("le 4e voyageur du vol est refuse car il n'y a plus de place",
                ticketVol4.getStatut() == StatutTicket.REFUSE);
        verifier("le vol n'a plus aucune place disponible apres les 3 reservations",
                vol.getPlacesDisponibles() == 0);

        tenterAnnulation(ticketBus1);
        verifier("apres l'annulation du 1er ticket, le bus a bien une place de plus",
                bus.getPlacesDisponibles() == 1);
        Ticket ticketBus5 = new Ticket(9105, busV4, bus, 3500);
        verifier("un 5e voyageur peut recuperer la place liberee par l'annulation",
                ticketBus5.getStatut() == StatutTicket.RESERVE);
    }

    private static void testCasLimitesEtValidation() {
        System.out.println("\n-- Test 7 : cas limites et validation des entrees --");

        Trajet busPileALaLimite = new TrajetBus(501, "Ouagadougou", "Kaya", "2026-10-01", "08:00", 2, 24, "STAF");
        Ticket ticketPileALaLimite = new Ticket(9501, new Voyageur(6, "TEST Limite", "limite@example.com"),
                busPileALaLimite, 2000);
        verifier("a exactement 24h (delai du bus), l'annulation n'est plus autorisee",
                !ticketPileALaLimite.peutEtreAnnule());

        Trajet volPileALaLimite = new TrajetVol(503, "Ouagadougou", "Niamey", "2026-10-01", "08:00", 2, 73, "HF 300");
        Ticket ticketVolLimite = new Ticket(9503, new Voyageur(6, "TEST Limite", "limite@example.com"),
                volPileALaLimite, 2000);
        verifier("a 73h d'un vol (delai de 72h), l'annulation est encore autorisee",
                ticketVolLimite.peutEtreAnnule());

        verifierException("un nom de voyageur vide est refuse", IllegalArgumentException.class,
                () -> new Voyageur(7, "", "vide@example.com"));
        verifierException("un email sans '@' est refuse", IllegalArgumentException.class,
                () -> new Voyageur(8, "TEST Email", "pas-un-email"));
        verifierException("un prix negatif est refuse a la creation du ticket", IllegalArgumentException.class,
                () -> new Ticket(9502, new Voyageur(9, "TEST Prix", "prix@example.com"), busPileALaLimite, -100));
        verifierException("un trajet avec un nombre de places negatif est refuse", IllegalArgumentException.class,
                () -> new TrajetBus(502, "Ouagadougou", "Po", "2026-10-01", "08:00", -1, 48, "STAF"));
    }

    private static void testDepotGenerique() {
        System.out.println("\n-- Test 8 : depot generique Depot<T> --");
        Depot<Voyageur> depotVoyageurs = new Depot<>("Voyageur");
        Depot<Trajet> depotTrajets = new Depot<>("Trajet");

        try {
            depotVoyageurs.ajouter(1, new Voyageur(1, "SIONE Yasser", "yasser@example.com"));
            depotTrajets.ajouter(10, new TrajetBus(10, "Ouagadougou", "Kaya", "2026-10-01", "08:00", 2, 48, "STAF"));
            verifier("un meme Depot generique accepte des Voyageur ou des Trajet selon son type", true);
        } catch (DoublonException e) {
            verifier("un meme Depot generique accepte des Voyageur ou des Trajet selon son type", false);
        }

        verifier("la taille du depot de voyageurs est 1", depotVoyageurs.taille() == 1);
        verifier("contient() retrouve une cle presente", depotVoyageurs.contient(1));
        verifier("contient() refuse une cle absente", !depotVoyageurs.contient(2));

        try {
            Voyageur trouve = depotVoyageurs.trouver(1);
            verifier("trouver() renvoie un Voyageur sans transtypage", trouve.getNom().equals("SIONE Yasser"));
        } catch (ElementIntrouvableException e) {
            verifier("trouver() renvoie un Voyageur sans transtypage", false);
        }

        verifierException("ajouter une cle deja utilisee leve DoublonException", DoublonException.class,
                () -> depotVoyageurs.ajouter(1, new Voyageur(1, "AUTRE", "autre@example.com")));
        verifierException("trouver une cle absente leve ElementIntrouvableException",
                ElementIntrouvableException.class, () -> depotVoyageurs.trouver(99));
        verifierException("ajouter un element nul est refuse", IllegalArgumentException.class,
                () -> depotVoyageurs.ajouter(5, null));
        verifier("le depot n'a pas change apres les refus", depotVoyageurs.taille() == 1);

        List<Voyageur> copie = depotVoyageurs.tous();
        copie.clear();
        verifier("modifier la liste renvoyee par tous() ne modifie pas le depot", depotVoyageurs.taille() == 1);
    }

    private static void testEqualsEtHashCode() {
        System.out.println("\n-- Test 9 : contrat equals() et hashCode() avec HashSet --");
        Voyageur original = new Voyageur(1, "SIONE Yasser", "yasser@example.com");
        Voyageur memeNumero = new Voyageur(1, "SIONE Y.", "autre@example.com");
        Voyageur autre = new Voyageur(2, "YELKUNI Franck", "franck@example.com");

        verifier("deux voyageurs de meme numero sont equals", original.equals(memeNumero));
        verifier("deux objets equals ont le meme hashCode", original.hashCode() == memeNumero.hashCode());
        verifier("deux voyageurs de numeros differents ne sont pas equals", !original.equals(autre));

        Set<Voyageur> ensemble = new HashSet<>();
        ensemble.add(original);
        ensemble.add(memeNumero);
        ensemble.add(autre);
        verifier("un HashSet elimine le doublon de voyageur", ensemble.size() == 2);

        Trajet trajetA = new TrajetBus(10, "Ouagadougou", "Kaya", "2026-10-01", "08:00", 2, 48, "STAF");
        Trajet trajetB = new TrajetBus(10, "Ouagadougou", "Po", "2026-10-02", "09:00", 5, 96, "TSR");
        Set<Trajet> trajets = new HashSet<>();
        trajets.add(trajetA);
        trajets.add(trajetB);
        verifier("un HashSet elimine le doublon de trajet de meme numero", trajets.size() == 1);

        Voyageur voyageur = new Voyageur(1, "SIONE Yasser", "yasser@example.com");
        Ticket ticketA = new Ticket(1, voyageur, trajetA, 1000);
        Ticket ticketB = new Ticket(1, voyageur, trajetA, 1000);
        verifier("deux tickets de meme numero sont equals", ticketA.equals(ticketB));
        verifier("un voyageur n'est jamais equals a null ni a un autre type",
                !original.equals(null) && !original.equals("texte"));
    }

    private static void testTris() {
        System.out.println("\n-- Test 10 : tris avec Comparable et Comparator --");
        Trajet tardif = new TrajetBus(1, "Ouagadougou", "Kaya", "2026-12-01", "08:00", 2, 48, "STAF");
        Trajet tot = new TrajetBus(2, "Ouagadougou", "Po", "2026-10-01", "08:00", 2, 48, "STAF");
        Trajet memeJourApres = new TrajetVol(3, "Ouagadougou", "Lome", "2026-10-01", "15:00", 2, 48, "ET 100");

        List<Trajet> trajets = new ArrayList<>();
        trajets.add(tardif);
        trajets.add(memeJourApres);
        trajets.add(tot);
        Collections.sort(trajets);
        verifier("les trajets sont tries par date puis par heure",
                trajets.get(0) == tot && trajets.get(1) == memeJourApres && trajets.get(2) == tardif);
        verifier("compareTo renvoie une valeur negative pour un trajet plus proche", tot.compareTo(tardif) < 0);
        verifier("compareTo renvoie 0 pour un meme trajet", tot.compareTo(tot) == 0);

        List<Voyageur> voyageurs = new ArrayList<>();
        voyageurs.add(new Voyageur(3, "ZONGO Paul", "paul@example.com"));
        voyageurs.add(new Voyageur(1, "BAMBARA Awa", "awa@example.com"));
        voyageurs.add(new Voyageur(2, "OUEDRAOGO Salif", "salif@example.com"));

        Collections.sort(voyageurs);
        verifier("l'ordre naturel des voyageurs suit leur numero", voyageurs.get(0).getNumero() == 1
                && voyageurs.get(2).getNumero() == 3);

        Collections.sort(voyageurs, new ComparateurVoyageurParNom());
        verifier("le Comparator trie les voyageurs par nom",
                voyageurs.get(0).getNom().equals("BAMBARA Awa")
                        && voyageurs.get(1).getNom().equals("OUEDRAOGO Salif")
                        && voyageurs.get(2).getNom().equals("ZONGO Paul"));
    }

    private static void testAgenceEnregistrement() throws Exception {
        System.out.println("\n-- Test 11 : agence, enregistrement et doublons --");
        Agence agence = creerAgenceDeTest();

        verifier("l'agence compte 4 voyageurs et 3 trajets", agence.nombreVoyageurs() == 4 && agence.nombreTrajets() == 3);
        verifierException("un email deja utilise est refuse (sans tenir compte de la casse)", DoublonException.class,
                () -> agence.enregistrerVoyageur(new Voyageur(9, "Copie", "YASSER@example.com")));
        verifierException("un numero de voyageur deja utilise est refuse", DoublonException.class,
                () -> agence.enregistrerVoyageur(new Voyageur(1, "Copie", "copie@example.com")));
        verifierException("un numero de trajet deja utilise est refuse", DoublonException.class,
                () -> agence.ajouterTrajet(new TrajetBus(10, "Ouagadougou", "Po", "2026-10-30", "09:00", 3, 96, "STAF")));
        verifier("les refus ne changent pas le nombre de voyageurs ni de trajets",
                agence.nombreVoyageurs() == 4 && agence.nombreTrajets() == 3);

        agence.enregistrerVoyageur(new Voyageur(9, "Copie", "copie@example.com"));
        verifier("un email refuse pour un numero deja pris reste utilisable pour un autre numero",
                agence.nombreVoyageurs() == 5);
    }

    private static void testAgenceReservation() throws Exception {
        System.out.println("\n-- Test 12 : agence, reservation et erreurs metier --");
        Agence agence = creerAgenceDeTest();

        Ticket ticket = agence.reserver(1, 10, 5000);
        verifier("reserver() cree un ticket RESERVE et numerote", ticket.getStatut() == StatutTicket.RESERVE
                && ticket.getNumero() == 1);
        verifier("le ticket est enregistre dans l'agence", agence.nombreTickets() == 1);
        verifier("une place a ete prise sur le trajet 10", agence.chercherTrajet(10).getPlacesDisponibles() == 1);

        verifierException("un voyageur inconnu leve ElementIntrouvableException", ElementIntrouvableException.class,
                () -> agence.reserver(99, 10, 5000));
        verifierException("un trajet inconnu leve ElementIntrouvableException", ElementIntrouvableException.class,
                () -> agence.reserver(1, 99, 5000));
        verifierException("une double reservation leve ReservationInvalideException",
                ReservationInvalideException.class, () -> agence.reserver(1, 10, 5000));
        verifierException("un prix negatif leve ReservationInvalideException",
                ReservationInvalideException.class, () -> agence.reserver(2, 10, -5));

        verifier("les refus n'ont cree aucun ticket", agence.nombreTickets() == 1);
        verifier("les refus n'ont pris aucune place", agence.chercherTrajet(10).getPlacesDisponibles() == 1);

        agence.annuler(ticket.getNumero());
        Ticket nouveau = agence.reserver(1, 10, 5000);
        verifier("apres une annulation, le meme voyageur peut reserver a nouveau",
                nouveau.getStatut() == StatutTicket.RESERVE);
        verifier("les tickets du voyageur 1 sont bien retrouves", agence.ticketsDuVoyageur(1).size() == 2);
    }

    private static void testFileAttente() throws Exception {
        System.out.println("\n-- Test 13 : file d'attente sur un trajet complet --");
        Agence agence = creerAgenceDeTest();

        Ticket surComplet = agence.reserver(1, 30, 4000);
        verifier("un ticket sur un trajet complet passe EN_ATTENTE", surComplet.getStatut() == StatutTicket.EN_ATTENTE);
        verifier("la file d'attente du trajet 30 contient 1 ticket", agence.tailleFileAttente(30) == 1);
        verifierException("un voyageur deja en attente ne peut pas se remettre en file",
                ReservationInvalideException.class, () -> agence.reserver(1, 30, 4000));

        Ticket premier = agence.reserver(1, 20, 95000);
        Ticket deuxieme = agence.reserver(2, 20, 95000);
        Ticket troisieme = agence.reserver(3, 20, 95000);
        verifier("le vol de 1 place accepte le premier voyageur", premier.getStatut() == StatutTicket.RESERVE);
        verifier("le deuxieme voyageur est en attente", deuxieme.getStatut() == StatutTicket.EN_ATTENTE);
        verifier("le troisieme voyageur est en attente", troisieme.getStatut() == StatutTicket.EN_ATTENTE);
        verifier("la file du trajet 20 contient 2 tickets", agence.tailleFileAttente(20) == 2);

        Agence autre = creerAgenceDeTest();
        Ticket a = autre.reserver(1, 10, 3000);
        Ticket b = autre.reserver(2, 10, 3000);
        Ticket c = autre.reserver(3, 10, 3000);
        Ticket d = autre.reserver(4, 10, 3000);
        verifier("le bus de 2 places met le 3e et le 4e voyageur en attente",
                c.getStatut() == StatutTicket.EN_ATTENTE && d.getStatut() == StatutTicket.EN_ATTENTE);

        autre.annuler(a.getNumero());
        verifier("l'annulation promeut le premier de la file (ordre d'arrivee respecte)",
                c.getStatut() == StatutTicket.RESERVE && d.getStatut() == StatutTicket.EN_ATTENTE);
        verifier("le trajet reste complet apres la promotion", autre.chercherTrajet(10).estComplet());
        verifier("la file ne contient plus que le 4e voyageur", autre.tailleFileAttente(10) == 1);

        autre.annuler(d.getNumero());
        verifier("annuler un ticket EN_ATTENTE le passe ANNULE", d.getStatut() == StatutTicket.ANNULE);
        verifier("annuler un ticket EN_ATTENTE ne libere aucune place", autre.chercherTrajet(10).estComplet());
        verifier("la file est vide apres le retrait", autre.tailleFileAttente(10) == 0);
        verifier("le ticket b n'a pas ete touche", b.getStatut() == StatutTicket.RESERVE);
    }

    private static void testValidationEtStatistiques() throws Exception {
        System.out.println("\n-- Test 14 : validation au depart et statistiques --");
        Agence agence = creerAgenceDeTest();

        Ticket t1 = agence.reserver(1, 10, 5000);
        Ticket t2 = agence.reserver(2, 10, 5000);
        Ticket t3 = agence.reserver(3, 20, 95000);
        agence.annuler(t2.getNumero());

        int valides = agence.validerDepart(10);
        verifier("validerDepart() valide 1 seul ticket sur le trajet 10", valides == 1);
        verifier("le ticket reserve passe PAYE", t1.getStatut() == StatutTicket.PAYE);
        verifier("le ticket annule reste ANNULE", t2.getStatut() == StatutTicket.ANNULE);
        verifier("le ticket d'un autre trajet n'est pas touche", t3.getStatut() == StatutTicket.RESERVE);
        verifier("un deuxieme appel ne valide plus rien", agence.validerDepart(10) == 0);
        verifierException("valider un trajet inconnu leve ElementIntrouvableException",
                ElementIntrouvableException.class, () -> agence.validerDepart(99));

        Map<StatutTicket, Integer> compteurs = agence.compterParStatut();
        verifier("compterParStatut() compte 1 PAYE, 1 ANNULE et 1 RESERVE",
                compteurs.get(StatutTicket.PAYE) == 1 && compteurs.get(StatutTicket.ANNULE) == 1
                        && compteurs.get(StatutTicket.RESERVE) == 1);
        verifier("compterParStatut() renvoie 0 pour les statuts absents",
                compteurs.get(StatutTicket.REFUSE) == 0 && compteurs.get(StatutTicket.EN_ATTENTE) == 0);
        verifier("le chiffre d'affaires ne compte que les tickets PAYE", agence.chiffreAffaires() == 5000);

        agence.validerDepart(20);
        verifier("le chiffre d'affaires augmente apres validation du vol", agence.chiffreAffaires() == 100000);
    }

    private static void testEnsemblesEtRecherche() throws Exception {
        System.out.println("\n-- Test 15 : ensembles, tris et recherche --");
        Agence agence = creerAgenceDeTest();
        agence.reserver(1, 10, 5000);
        agence.reserver(2, 10, 5000);
        agence.reserver(2, 20, 95000);
        agence.reserver(3, 20, 95000);

        Set<Voyageur> communs = agence.voyageursCommuns(10, 20);
        verifier("un seul voyageur est present sur les trajets 10 et 20", communs.size() == 1);
        verifier("ce voyageur est le numero 2", communs.contains(agence.chercherVoyageur(2)));
        verifier("voyageursDuTrajet(10) renvoie 2 voyageurs", agence.voyageursDuTrajet(10).size() == 2);

        verifier("les trajets complets sont exclus des trajets disponibles", agence.trajetsDisponibles().isEmpty());
        agence.annuler(1);
        List<Trajet> disponibles = agence.trajetsDisponibles();
        verifier("un trajet redevient disponible apres une annulation", disponibles.size() == 1
                && disponibles.get(0).getNumero() == 10);

        List<Trajet> recherche = agence.rechercherTrajets("ouagadougou", "BOBO-DIOULASSO");
        verifier("la recherche ignore la casse", recherche.size() == 1 && recherche.get(0).getNumero() == 10);
        verifier("une recherche sans resultat renvoie une liste vide",
                agence.rechercherTrajets("Ouagadougou", "Paris").isEmpty());

        Set<String> villes = agence.villesDesservies();
        verifier("villesDesservies() est un TreeSet trie sans doublon",
                villes.toString().equals("[Abidjan, Bobo-Dioulasso, Koudougou]"));

        List<Voyageur> tries = agence.voyageursTries(new ComparateurVoyageurParNom());
        verifier("voyageursTries() suit l'ordre alphabetique des noms",
                tries.get(0).getNom().equals("NADEMBEGA Ingrid") && tries.get(3).getNom().equals("YELKUNI Franck"));
    }

    private static void testAtomicite() throws Exception {
        System.out.println("\n-- Test 16 : atomicite, un refus ne laisse aucune trace --");
        Agence agence = creerAgenceDeTest();
        Ticket vol = agence.reserver(1, 20, 95000);

        int ticketsAvant = agence.nombreTickets();
        int placesAvant = agence.chercherTrajet(10).getPlacesDisponibles();
        StatutTicket statutAvant = vol.getStatut();

        verifierException("annuler un vol hors delai leve AnnulationImpossibleException",
                AnnulationImpossibleException.class, () -> agence.annuler(vol.getNumero()));
        verifier("le statut du ticket est inchange", vol.getStatut() == statutAvant);
        verifier("la place du vol n'est pas rendue", agence.chercherTrajet(20).estComplet());

        verifierException("reserver pour un voyageur inconnu leve ElementIntrouvableException",
                ElementIntrouvableException.class, () -> agence.reserver(99, 10, 1000));
        verifierException("annuler un ticket inconnu leve ElementIntrouvableException",
                ElementIntrouvableException.class, () -> agence.annuler(999));
        verifier("le nombre de tickets est inchange apres les refus", agence.nombreTickets() == ticketsAvant);
        verifier("les places du trajet 10 sont inchangees apres les refus",
                agence.chercherTrajet(10).getPlacesDisponibles() == placesAvant);

        Trajet unePlace = new TrajetBus(70, "Ouagadougou", "Dedougou", "2026-10-30", "07:00", 1, 96, "STAF");
        unePlace.reserverPlace();
        int avant = unePlace.getPlacesDisponibles();
        verifierException("reserverPlace() sur un trajet complet leve TrajetCompletException",
                TrajetCompletException.class, () -> unePlace.reserverPlace());
        verifier("les places restent a zero apres le refus (jamais negatives)",
                unePlace.getPlacesDisponibles() == avant && avant == 0);
    }

    private static void testHierarchieExceptions() throws Exception {
        System.out.println("\n-- Test 17 : hierarchie des exceptions personnalisees --");
        Agence agence = creerAgenceDeTest();
        Trajet trajet = new TrajetBus(80, "Ouagadougou", "Tenkodogo", "2026-10-30", "07:00", 0, 96, "STAF");

        verifierException("TrajetCompletException est attrapee comme BilletterieException",
                BilletterieException.class, () -> trajet.reserverPlace());
        verifierException("DoublonException est attrapee comme BilletterieException",
                BilletterieException.class,
                () -> agence.enregistrerVoyageur(new Voyageur(1, "Copie", "copie@example.com")));
        verifierException("ElementIntrouvableException est attrapee comme BilletterieException",
                BilletterieException.class, () -> agence.chercherTrajet(999));
        verifierException("ReservationInvalideException est attrapee comme BilletterieException",
                BilletterieException.class, () -> agence.reserver(1, 10, -1));

        agence.reserver(1, 20, 95000);
        verifierException("AnnulationImpossibleException est attrapee comme BilletterieException",
                BilletterieException.class, () -> agence.annuler(1));

        int erreursCapturees = 0;
        try {
            agence.chercherVoyageur(999);
        } catch (BilletterieException e) {
            erreursCapturees++;
        } finally {
            erreursCapturees++;
        }
        verifier("le bloc finally s'execute apres le catch", erreursCapturees == 2);
    }

    private static void testValidationTrajets() {
        System.out.println("\n-- Test 18 : validation des trajets, bus et vols --");
        verifierException("deux villes identiques sont refusees", IllegalArgumentException.class,
                () -> new TrajetBus(1, "Ouagadougou", "ouagadougou", "2026-10-01", "08:00", 2, 48, "STAF"));
        verifierException("une ville de depart vide est refusee", IllegalArgumentException.class,
                () -> new TrajetBus(2, "", "Kaya", "2026-10-01", "08:00", 2, 48, "STAF"));
        verifierException("un delai negatif avant depart est refuse", IllegalArgumentException.class,
                () -> new TrajetVol(3, "Ouagadougou", "Kaya", "2026-10-01", "08:00", 2, -5, "HF 100"));
        verifierException("une compagnie de bus vide est refusee", IllegalArgumentException.class,
                () -> new TrajetBus(4, "Ouagadougou", "Kaya", "2026-10-01", "08:00", 2, 48, " "));
        verifierException("un numero de vol vide est refuse", IllegalArgumentException.class,
                () -> new TrajetVol(5, "Ouagadougou", "Kaya", "2026-10-01", "08:00", 2, 48, ""));
        verifierException("un ticket sans voyageur est refuse", IllegalArgumentException.class,
                () -> new Ticket(1, null, new TrajetBus(6, "Ouagadougou", "Kaya", "2026-10-01", "08:00", 2, 48, "STAF"), 100));
        verifierException("une agence sans nom est refusee", IllegalArgumentException.class,
                () -> new Agence("  "));

        Trajet bus = new TrajetBus(7, "Ouagadougou", "Kaya", "2026-10-01", "08:00", 2, 48, "STAF");
        Trajet vol = new TrajetVol(8, "Ouagadougou", "Niamey", "2026-10-01", "08:00", 2, 48, "HF 200");
        verifier("un bus a un delai d'annulation de 24h", bus.delaiAnnulationHeures() == 24);
        verifier("un vol a un delai d'annulation de 72h", vol.delaiAnnulationHeures() == 72);
        verifier("formaterPrix() separe les milliers par des espaces",
                Ticket.formaterPrix(275000).replace('\u202f', ' ').replace('\u00a0', ' ').equals("275 000"));
    }

    private static void testTrain() throws Exception {
        System.out.println("\n-- Test 19 : trajets en train et polymorphisme --");
        Trajet train = new TrajetTrain(90, "Ouagadougou", "Banfora", "2026-11-15", "05:30", 2, 200, "SIT 12");
        Trajet bus = new TrajetBus(91, "Ouagadougou", "Kaya", "2026-11-15", "06:00", 2, 200, "STAF");
        Trajet vol = new TrajetVol(92, "Ouagadougou", "Niamey", "2026-11-15", "07:00", 2, 200, "HF 300");

        Trajet[] trajets = {bus, vol, train};
        int[] attendus = {24, 72, 48};
        boolean tousCorrects = true;
        for (int i = 0; i < trajets.length; i++) {
            if (trajets[i].delaiAnnulationHeures() != attendus[i]) {
                tousCorrects = false;
            }
        }
        verifier("les trois types de trajet donnent leur propre delai d'annulation", tousCorrects);

        Voyageur voyageur = new Voyageur(1, "SIONE Yasser", "yasser@example.com");
        Trajet trainAnnulable = new TrajetTrain(93, "Ouagadougou", "Banfora", "2026-11-15", "05:30", 2, 49, "SIT 13");
        Trajet trainLimite = new TrajetTrain(94, "Ouagadougou", "Banfora", "2026-11-15", "05:30", 2, 48, "SIT 14");
        verifier("un train a 49h du depart peut etre annule", new Ticket(1, voyageur, trainAnnulable, 1000).peutEtreAnnule());
        verifier("un train a exactement 48h du depart ne peut plus etre annule",
                !new Ticket(2, voyageur, trainLimite, 1000).peutEtreAnnule());
        verifier("le numero de train est conserve", ((TrajetTrain) train).getNumeroTrain().equals("SIT 12"));
        verifierException("un numero de train vide est refuse", IllegalArgumentException.class,
                () -> new TrajetTrain(95, "Ouagadougou", "Banfora", "2026-11-15", "05:30", 2, 200, " "));

        Affichable[] elements = {train, bus, vol};
        int affiches = 0;
        for (Affichable element : elements) {
            element.afficher();
            affiches++;
        }
        verifier("un train se traite comme n'importe quel Affichable", affiches == 3);

        Agence agence = creerAgenceDeTest();
        agence.ajouterTrajet(train);
        Ticket ticket = agence.reserver(1, 90, 12000);
        agence.annuler(ticket.getNumero());
        verifier("l'agence annule un ticket de train dans les delais", ticket.getStatut() == StatutTicket.ANNULE);
        verifier("la place du train est rendue apres l'annulation", train.getPlacesDisponibles() == 2);
    }

    private static Agence agenceAvecHistorique() throws BilletterieException {
        Agence agence = creerAgenceDeTest();
        agence.reserver(1, 10, 5000);
        agence.reserver(2, 10, 5000);
        agence.reserver(3, 10, 5000);
        agence.reserver(4, 30, 4000);
        agence.reserver(1, 20, 95000);
        agence.validerDepart(20);
        return agence;
    }

    private static void testSauvegarde() throws Exception {
        System.out.println("\n-- Test 20 : sauvegarde et chargement par serialisation --");
        Agence agence = agenceAvecHistorique();
        Path fichier = Files.createTempFile("agence-test", ".bin");
        try {
            Sauvegarde.sauvegarder(agence, fichier.toString());
            Agence rechargee = Sauvegarde.charger(fichier.toString());

            verifier("l'agence rechargee a le meme nom", rechargee.getNom().equals(agence.getNom()));
            verifier("l'agence rechargee a le meme nombre de voyageurs et de trajets",
                    rechargee.nombreVoyageurs() == agence.nombreVoyageurs()
                            && rechargee.nombreTrajets() == agence.nombreTrajets());
            verifier("l'agence rechargee a le meme nombre de tickets", rechargee.nombreTickets() == agence.nombreTickets());
            verifier("les statuts des tickets sont conserves", rechargee.compterParStatut().equals(agence.compterParStatut()));
            verifier("le chiffre d'affaires est conserve", rechargee.chiffreAffaires() == agence.chiffreAffaires());
            verifier("la file d'attente est conservee", rechargee.tailleFileAttente(30) == 1);
            verifier("les places des trajets sont conservees",
                    rechargee.chercherTrajet(10).getPlacesDisponibles() == agence.chercherTrajet(10).getPlacesDisponibles());

            agence.reserver(4, 10, 5000);
            verifier("l'agence rechargee est independante de l'originale",
                    rechargee.nombreTickets() == agence.nombreTickets() - 1);

            Ticket enAttente = rechargee.ticketsDuVoyageur(4).get(0);
            rechargee.annuler(rechargee.ticketsDuVoyageur(1).get(0).getNumero());
            verifier("l'agence rechargee continue de fonctionner (annulation puis reservation)",
                    rechargee.ticketsDuVoyageur(1).get(0).getStatut() == StatutTicket.ANNULE
                            && enAttente.getStatut() == StatutTicket.EN_ATTENTE);

            Sauvegarde.sauvegarder(rechargee, fichier.toString());
            verifier("une seconde sauvegarde remplace la premiere",
                    Sauvegarde.charger(fichier.toString()).nombreTickets() == rechargee.nombreTickets());
        } finally {
            Files.deleteIfExists(fichier);
        }

        verifierException("charger un fichier absent leve SauvegardeException", SauvegardeException.class,
                () -> Sauvegarde.charger("dossier-inexistant/agence.bin"));
        Path dossierInexistant = fichier.getParent().resolve("dossier-qui-n-existe-pas").resolve("agence.bin");
        verifierException("sauvegarder dans un dossier inexistant leve SauvegardeException",
                SauvegardeException.class, () -> Sauvegarde.sauvegarder(agence, dossierInexistant.toString()));
        verifier("aucun fichier n'est cree apres une sauvegarde refusee", !Files.exists(dossierInexistant));
        verifierException("un chemin vide est refuse", IllegalArgumentException.class,
                () -> Sauvegarde.sauvegarder(agence, " "));
        verifierException("une agence nulle est refusee", IllegalArgumentException.class,
                () -> Sauvegarde.sauvegarder(null, fichier.toString()));
        verifierException("SauvegardeException est capturee comme BilletterieException", BilletterieException.class,
                () -> Sauvegarde.charger("absent.bin"));

        Path invalide = Files.createTempFile("agence-invalide", ".bin");
        try {
            Files.write(invalide, new byte[] {1, 2, 3, 4});
            verifierException("un fichier qui n'est pas une sauvegarde leve SauvegardeException",
                    SauvegardeException.class, () -> Sauvegarde.charger(invalide.toString()));
        } finally {
            Files.deleteIfExists(invalide);
        }
    }

    private static String executerMenu(Agence agence, String saisie) {
        ByteArrayOutputStream tampon = new ByteArrayOutputStream();
        PrintStream sortie = new PrintStream(tampon);
        new MenuConsole(agence, new ByteArrayInputStream(saisie.getBytes()), sortie).lancer();
        sortie.flush();
        return tampon.toString();
    }

    private static void testMenu() throws Exception {
        System.out.println("\n-- Test 21 : menu interactif de la console --");
        Agence agence = creerAgenceDeTest();

        verifier("le choix 0 quitte le menu", executerMenu(agence, "0\n").contains("Au revoir."));
        verifier("un choix inconnu est signale", executerMenu(agence, "99\n0\n").contains("Choix inconnu : 99"));
        String sortieTexte = executerMenu(agence, "abc\n0\n");
        verifier("une saisie non numerique est signalee sans arreter le menu",
                sortieTexte.contains("Saisie invalide") && sortieTexte.contains("Au revoir."));
        verifier("la fin de la saisie ferme proprement le menu", executerMenu(agence, "1\n").contains("Fin de la saisie."));
        verifier("le menu liste les trajets disponibles", executerMenu(agence, "1\n0\n").contains("Trajet 10"));
        verifier("le menu cherche un trajet par villes",
                executerMenu(agence, "2\nOuagadougou\nAbidjan\n0\n").contains("Trajet 20"));
        verifier("une recherche sans resultat est signalee",
                executerMenu(agence, "2\nOuagadougou\nParis\n0\n").contains("Aucun trajet trouve."));

        executerMenu(agence, "3\n50\nTest Menu\nmenu@example.com\n0\n");
        verifier("le menu enregistre un voyageur", agence.nombreVoyageurs() == 5);
        String doublon = executerMenu(agence, "3\n51\nCopie\nyasser@example.com\n0\n");
        verifier("un email deja utilise est refuse par le menu",
                doublon.contains("Erreur") && agence.nombreVoyageurs() == 5);
        verifier("un email invalide est refuse par le menu",
                executerMenu(agence, "3\n52\nNom\npas-un-email\n0\n").contains("Saisie invalide"));

        String reservation = executerMenu(agence, "4\n1\n10\n5000\n0\n");
        verifier("le menu reserve une place", reservation.contains("RESERVE") && agence.nombreTickets() == 1);
        verifier("un prix non numerique ne cree aucun ticket",
                executerMenu(agence, "4\n2\n10\ncinq\n0\n").contains("Saisie invalide") && agence.nombreTickets() == 1);
        String attente = executerMenu(agence, "4\n1\n30\n4000\n0\n");
        verifier("le menu annonce la position dans la file d'attente", attente.contains("position 1"));
        verifier("le menu annule un ticket", executerMenu(agence, "5\n1\n0\n").contains("Ticket 1 annule."));
        verifier("annuler un ticket inconnu est signale", executerMenu(agence, "5\n999\n0\n").contains("Erreur"));
        verifier("le menu valide un depart", executerMenu(agence, "6\n10\n0\n").contains("ticket(s) valide(s)."));
        verifier("le menu affiche les tickets d'un voyageur", executerMenu(agence, "7\n1\n0\n").contains("Ticket"));
        verifier("le menu affiche les statistiques", executerMenu(agence, "8\n0\n").contains("Chiffre d'affaires"));

        Path fichier = Files.createTempFile("agence-menu", ".bin");
        try {
            executerMenu(agence, "9\n" + fichier + "\n0\n");
            verifier("le menu sauvegarde l'agence dans un fichier", Files.size(fichier) > 0);
            Agence autre = creerAgenceDeTest();
            MenuConsole menu = new MenuConsole(autre,
                    new ByteArrayInputStream(("10\n" + fichier + "\n0\n").getBytes()), new PrintStream(new ByteArrayOutputStream()));
            menu.lancer();
            verifier("le menu charge une agence depuis un fichier", menu.getAgence().nombreTickets() == agence.nombreTickets());
        } finally {
            Files.deleteIfExists(fichier);
        }
        verifier("charger un fichier absent par le menu est signale",
                executerMenu(agence, "10\nabsent.bin\n0\n").contains("Erreur"));
        verifierException("un menu sans agence est refuse", IllegalArgumentException.class,
                () -> new MenuConsole(null, new ByteArrayInputStream(new byte[0]), System.out));
    }
}
