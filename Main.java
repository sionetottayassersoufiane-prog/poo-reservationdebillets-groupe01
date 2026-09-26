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
        ticket1.annuler();
        ticket1.afficher();
        t1.afficher(); 

        System.out.println("\n=== Reservation sur le vol ===");
        Ticket ticket2 = new Ticket(1002, v2, t2, 95000);
        ticket2.afficher();

        System.out.println("\n=== Annulation refusee (vol, depart dans moins de 72h) ===");
        ticket2.annuler();
        ticket2.afficher();

        System.out.println("\n=== Validation automatique au depart ===");
        ticket2.validerAuto();
        ticket2.afficher();

        System.out.println("\n=== Verification anti double-reservation ===");
        System.out.println("Le ticket 1002 est-il deja reserve/paye ? " + ticket2.estDejaReserve());

        System.out.println("\n=== Trajet deja complet des la creation ===");
        Trajet t3 = new TrajetBus(103, "Ouagadougou", "Ouahigouya", "2026-09-21", "10:00", 0, 96, "STAF");
        Ticket ticket3 = new Ticket(1003, v1, t3, 4500);
        ticket3.afficher();
    }
}
