public class Main {
    public static void main(String[] args) {
        Voyageur v1 = new Voyageur(1, "SIONE Yasser", "sioneyasser@gmail.com");
        Voyageur v2 = new Voyageur(2, "YELKUNI Franck", "yelkunifranck@gmail.com");

        Trajet t1 = new TrajetBus(101, "Ouagadougou", "Bobo-Dioulasso", "2026-09-20", "08:00", 2, 120, "STAF");
        Trajet t2 = new TrajetVol(102, "Ouagadougou", "Abidjan", "2026-09-09", "07:00", 1, 48, "AH 512");

        System.out.println("=== Voyageurs et trajets crees ===");
        v1.afficher();
        v2.afficher();

        Trajet[] trajets = { t1, t2 };
        for (Trajet t : trajets) {
            t.afficher();
        }

        System.out.println("\n=== Reservation sur le bus ===");
        Ticket ticket1 = new Ticket(1001, v1, t1, 5000);
        ticket1.afficher();
        t1.afficher();

        System.out.println("\n=== Annulation acceptee (bus, depart dans plus de 24h) ===");
        annulerEtAfficher(ticket1);
        t1.afficher();

        System.out.println("\n=== Reservation sur le vol ===");
        Ticket ticket2 = new Ticket(1002, v2, t2, 95000);
        ticket2.afficher();

        System.out.println("\n=== Annulation refusee (vol, depart dans moins de 72h) ===");
        annulerEtAfficher(ticket2);

        System.out.println("\n=== Validation automatique au depart ===");
        ticket2.validerAuto();
        ticket2.afficher();

        System.out.println("\n=== Verification anti double-reservation ===");
        System.out.println("Le ticket 1002 est-il deja reserve/paye ? " + ticket2.estDejaReserve());

        System.out.println("\n=== Trajet deja complet des la creation ===");
        Trajet t3 = new TrajetBus(103, "Ouagadougou", "Ouahigouya", "2026-09-21", "10:00", 0, 96, "STAF");
        Ticket ticket3 = new Ticket(1003, v1, t3, 4500);
        ticket3.afficher();

        System.out.println("\n=== Test de fiabilite : 4 voyageurs sur un bus avec 3 places ===");
        Trajet busGroupe = new TrajetBus(104, "Ouagadougou", "Fada N'Gourma", "2026-11-01", "06:00", 3, 120, "STAF");
        Voyageur busVoyageur1 = new Voyageur(10, "SIONE Totta Yasser Soufiane", "sioneyasser@gmail.com");
        Voyageur busVoyageur2 = new Voyageur(11, "YELKUNI Tindwende Franck Onel", "yelkunifranck@gmail.com");
        Voyageur busVoyageur3 = new Voyageur(12, "NIKIEMA P. Hanifah", "nikiemahanifah@gmail.com");
        Voyageur busVoyageur4 = new Voyageur(13, "NADEMBEGA Ingrid Oceane", "nadembegaingrid@gmail.com");

        Ticket ticketBus1 = new Ticket(1101, busVoyageur1, busGroupe, 3500);
        Ticket ticketBus2 = new Ticket(1102, busVoyageur2, busGroupe, 3500);
        Ticket ticketBus3 = new Ticket(1103, busVoyageur3, busGroupe, 3500);
        Ticket ticketBus4 = new Ticket(1104, busVoyageur4, busGroupe, 3500);

        ticketBus1.afficher();
        ticketBus2.afficher();
        ticketBus3.afficher();
        ticketBus4.afficher();
        busGroupe.afficher();

        System.out.println("\n=== Test de fiabilite : 4 voyageurs sur un vol avec 3 places ===");
        Trajet volGroupe = new TrajetVol(105, "Ouagadougou", "Marseille", "2026-11-10", "22:00", 3, 100, "AF 900");
        Voyageur volVoyageur1 = new Voyageur(20, "SIONE Totta Yasser Soufiane", "sioneyasser@gmail.com");
        Voyageur volVoyageur2 = new Voyageur(21, "YELKUNI Tindwende Franck Onel", "yelkunifranck@gmail.com");
        Voyageur volVoyageur3 = new Voyageur(22, "NIKIEMA P. Hanifah", "nikiemahanifah@gmail.com");
        Voyageur volVoyageur4 = new Voyageur(23, "NADEMBEGA Ingrid Oceane", "nadembegaingrid@gmail.com");

        Ticket ticketVol1 = new Ticket(1201, volVoyageur1, volGroupe, 275000);
        Ticket ticketVol2 = new Ticket(1202, volVoyageur2, volGroupe, 275000);
        Ticket ticketVol3 = new Ticket(1203, volVoyageur3, volGroupe, 275000);
        Ticket ticketVol4 = new Ticket(1204, volVoyageur4, volGroupe, 275000);

        ticketVol1.afficher();
        ticketVol2.afficher();
        ticketVol3.afficher();
        ticketVol4.afficher();
        volGroupe.afficher();

        System.out.println("\n=== Une annulation libere bien la place pour un 5e voyageur ===");
        annulerEtAfficher(ticketBus1);
        busGroupe.afficher();
        Ticket ticketBus5 = new Ticket(1105, busVoyageur4, busGroupe, 3500);
        ticketBus5.afficher();
    }
    private static void annulerEtAfficher(Ticket ticket) {
        try {
            ticket.annuler();
        } catch (AnnulationImpossibleException e) {
            System.out.println(e.getMessage());
        }
        ticket.afficher();
    }
}
