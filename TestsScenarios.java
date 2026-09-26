public class TestsScenarios {

    private static int reussites = 0;
    private static int echecs = 0;

    public static void main(String[] args) {
        testInterfaceReservable();
        testReservationAcceptee();
        testTrajetComplet();
        testAnnulationAcceptee();
        testAnnulationRefusee();
        testOperationsInterditesSansEffet();
        testFiabiliteQuatreVoyageurs();
        testCasLimitesEtValidation();

        System.out.println("\n=== Bilan : " + reussites + " test(s) reussi(s), " + echecs + " echec(s) ===");
        if (echecs > 0) {
            System.exit(1);
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
    private static void testInterfaceReservable() {
        System.out.println("\n-- Test 0 : utilisation polymorphe de l'interface Reservable --");

        Reservable busReservable = new TrajetBus(401, "Ouagadougou", "Banfora", "2026-12-01", "07:00", 1, 96, "STAF");
        Reservable volReservable = new TrajetVol(402, "Ouagadougou", "Accra", "2026-12-02", "09:00", 1, 96, "AH 200");

        verifier("un TrajetBus vu comme Reservable n'est pas complet au depart",
                !busReservable.estComplet());
        verifier("un TrajetVol vu comme Reservable n'est pas complet au depart",
                !volReservable.estComplet());

        try {
            busReservable.reserverPlace();
            verifier("reserverPlace() via Reservable fonctionne sur le bus", true);
        } catch (TrajetCompletException e) {
            verifier("reserverPlace() via Reservable fonctionne sur le bus", false);
        }
        verifier("le bus vu comme Reservable est complet apres la reservation", busReservable.estComplet());

        try {
            volReservable.reserverPlace();
            verifier("reserverPlace() via Reservable fonctionne sur le vol", true);
        } catch (TrajetCompletException e) {
            verifier("reserverPlace() via Reservable fonctionne sur le vol", false);
        }

        try {
            busReservable.reserverPlace();
            verifier("reserverPlace() via Reservable echoue si le trajet est deja complet", false);
        } catch (TrajetCompletException e) {
            verifier("reserverPlace() via Reservable echoue si le trajet est deja complet", true);
        }

        busReservable.libererPlace();
        verifier("libererPlace() via Reservable rend la place disponible",
                !busReservable.estComplet());
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

        try {
            new Voyageur(7, "", "vide@example.com");
            verifier("un nom de voyageur vide est refuse", false);
        } catch (IllegalArgumentException e) {
            verifier("un nom de voyageur vide est refuse", true);
        }

        try {
            new Voyageur(8, "TEST Email", "pas-un-email");
            verifier("un email sans '@' est refuse", false);
        } catch (IllegalArgumentException e) {
            verifier("un email sans '@' est refuse", true);
        }

        try {
            new Ticket(9502, new Voyageur(9, "TEST Prix", "prix@example.com"), busPileALaLimite, -100);
            verifier("un prix negatif est refuse a la creation du ticket", false);
        } catch (IllegalArgumentException e) {
            verifier("un prix negatif est refuse a la creation du ticket", true);
        }

        try {
            new TrajetBus(502, "Ouagadougou", "Po", "2026-10-01", "08:00", -1, 48, "STAF");
            verifier("un trajet avec un nombre de places negatif est refuse", false);
        } catch (IllegalArgumentException e) {
            verifier("un trajet avec un nombre de places negatif est refuse", true);
        }
    }


    private static void tenterAnnulation(Ticket ticket) {
        try {
            ticket.annuler();
        } catch (AnnulationImpossibleException e) {
            System.out.println(e.getMessage());
        }
    }
}
