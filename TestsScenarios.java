public class TestsScenarios {

    private static int reussites = 0;
    private static int echecs = 0;

    public static void main(String[] args) {
        testReservationAcceptee();
        testTrajetComplet();
        testAnnulationAcceptee();
        testAnnulationRefusee();
        testOperationsInterditesSansEffet();

        System.out.println("\n=== Bilan : " + reussites + " test(s) reussi(s), " + echecs + " echec(s) ===");
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

    private static void testReservationAcceptee() {
        System.out.println("\n-- Test 1 : reservation acceptee --");
        Voyageur v = new Voyageur(1, "SIONE Yasser", "yasser@example.com");
        Trajet t = new TrajetBus(201, "Ouagadougou", "Bobo-Dioulasso", "2026-10-01", "08:00", 2, 120, "STAF");

        int placesAvant = t.getPlacesDisponibles();
        Ticket ticket = new Ticket(9001, v, t, 5000);

        verifier("le ticket est cree avec le statut Reserve", ticket.getStatut().equals("Reserve"));
        verifier("une place a bien ete prise sur le trajet", t.getPlacesDisponibles() == placesAvant - 1);
    }

    private static void testTrajetComplet() {
        System.out.println("\n-- Test 2 : trajet complet --");
        Voyageur v = new Voyageur(2, "YELKUNI Franck", "franck@example.com");
        Trajet t = new TrajetVol(202, "Ouagadougou", "Paris", "2026-10-05", "23:00", 0, 100, "AF 713");

        verifier("le trajet est bien annonce comme complet", t.estComplet());

        int placesAvant = t.getPlacesDisponibles();
        Ticket ticket = new Ticket(9002, v, t, 250000);

        verifier("le ticket nait avec le statut Refuse", ticket.getStatut().equals("Refuse"));
        verifier("aucune place n'a ete retiree d'un trajet deja complet", t.getPlacesDisponibles() == placesAvant);
    }

    private static void testAnnulationAcceptee() {
        System.out.println("\n-- Test 3 : annulation acceptee --");
        Voyageur v = new Voyageur(3, "NIKIEMA Hanifah", "hanifah@example.com");
        Trajet t = new TrajetBus(203, "Ouagadougou", "Koudougou", "2026-10-10", "09:00", 3, 48, "TSR");
        Ticket ticket = new Ticket(9003, v, t, 3000);

        int placesAvant = t.getPlacesDisponibles();
        ticket.annuler();

        verifier("le ticket passe au statut Annule", ticket.getStatut().equals("Annule"));
        verifier("la place annulee est bien reliberee", t.getPlacesDisponibles() == placesAvant + 1);
    }

    private static void testAnnulationRefusee() {
        System.out.println("\n-- Test 4 : annulation refusee --");
        Voyageur v = new Voyageur(4, "NADEMBEGA Ingrid", "ingrid@example.com");
        Trajet t = new TrajetVol(204, "Ouagadougou", "Dakar", "2026-10-02", "06:00", 2, 48, "HF 101");
        Ticket ticket = new Ticket(9004, v, t, 180000);

        String statutAvant = ticket.getStatut();
        int placesAvant = t.getPlacesDisponibles();
        ticket.annuler();

        verifier("le statut reste inchange quand le delai est depasse", ticket.getStatut().equals(statutAvant));
        verifier("aucune place n'est reliberee sur une annulation refusee", t.getPlacesDisponibles() == placesAvant);
    }

    private static void testOperationsInterditesSansEffet() {
        System.out.println("\n-- Test 5 : operations interdites sans effet sur l'etat --");
        Voyageur v = new Voyageur(5, "SIONE Yasser", "yasser2@example.com");
        Trajet t = new TrajetBus(205, "Ouagadougou", "Ouahigouya", "2026-10-15", "07:00", 2, 120, "STAF");
        Ticket ticket = new Ticket(9005, v, t, 4000);

        ticket.annuler();
        String statutApresAnnulation = ticket.getStatut();
        int placesApresAnnulation = t.getPlacesDisponibles();

        ticket.annuler();
        verifier("annuler un ticket deja Annule ne change pas son statut",
                ticket.getStatut().equals(statutApresAnnulation));
        verifier("annuler un ticket deja Annule ne libere pas de place supplementaire",
                t.getPlacesDisponibles() == placesApresAnnulation);

        ticket.validerAuto();
        verifier("valider un ticket Annule ne le fait pas passer a Paye",
                ticket.getStatut().equals(statutApresAnnulation));

        Trajet complet = new TrajetVol(206, "Ouagadougou", "Lome", "2026-10-20", "12:00", 0, 96, "ET 900");
        Ticket ticketRefuse = new Ticket(9006, v, complet, 210000);
        int placesAvantOperations = complet.getPlacesDisponibles();

        ticketRefuse.annuler();
        verifier("annuler un ticket Refuse ne change pas son statut",
                ticketRefuse.getStatut().equals("Refuse"));
        verifier("annuler un ticket Refuse ne modifie pas les places du trajet",
                complet.getPlacesDisponibles() == placesAvantOperations);
    }
