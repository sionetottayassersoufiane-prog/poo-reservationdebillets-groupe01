package billetterie.service;

import billetterie.contrats.Affichable;
import billetterie.exceptions.AnnulationImpossibleException;
import billetterie.exceptions.DoublonException;
import billetterie.exceptions.ElementIntrouvableException;
import billetterie.exceptions.ReservationInvalideException;
import billetterie.modele.StatutTicket;
import billetterie.modele.Ticket;
import billetterie.modele.Trajet;
import billetterie.modele.Voyageur;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Agence implements Affichable, Serializable {
    private static final long serialVersionUID = 1L;
    private final String nom;
    private final Depot<Voyageur> voyageurs;
    private final Depot<Trajet> trajets;
    private final HashSet<String> emailsUtilises;
    private final ArrayList<Ticket> tickets;
    private final HashMap<Integer, LinkedList<Ticket>> filesAttente;
    private int prochainNumeroTicket;

    public Agence(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'agence ne peut pas etre vide.");
        }
        this.nom = nom;
        this.voyageurs = new Depot<>("Voyageur");
        this.trajets = new Depot<>("Trajet");
        this.emailsUtilises = new HashSet<>();
        this.tickets = new ArrayList<>();
        this.filesAttente = new HashMap<>();
        this.prochainNumeroTicket = 1;
    }

    public void enregistrerVoyageur(Voyageur voyageur) throws DoublonException {
        if (voyageur == null) {
            throw new IllegalArgumentException("Le voyageur ne peut pas etre nul.");
        }
        String email = voyageur.getEmail().toLowerCase();
        if (emailsUtilises.contains(email)) {
            throw new DoublonException("L'email " + voyageur.getEmail() + " est deja utilise par un autre voyageur.");
        }
        voyageurs.ajouter(voyageur.getNumero(), voyageur);
        emailsUtilises.add(email);
    }

    public void ajouterTrajet(Trajet trajet) throws DoublonException {
        if (trajet == null) {
            throw new IllegalArgumentException("Le trajet ne peut pas etre nul.");
        }
        trajets.ajouter(trajet.getNumero(), trajet);
    }

    public Ticket reserver(int numeroVoyageur, int numeroTrajet, double prix)
            throws ElementIntrouvableException, ReservationInvalideException {
        Voyageur voyageur = voyageurs.trouver(numeroVoyageur);
        Trajet trajet = trajets.trouver(numeroTrajet);
        if (prix < 0) {
            throw new ReservationInvalideException("Le prix d'une reservation ne peut pas etre negatif.");
        }
        if (aDejaUnTicketActif(voyageur, trajet)) {
            throw new ReservationInvalideException("Reservation invalide : " + voyageur.getNom()
                    + " a deja un ticket actif sur le trajet " + trajet.getNumero() + ".");
        }
        Ticket ticket = new Ticket(prochainNumeroTicket, voyageur, trajet, prix);
        prochainNumeroTicket = prochainNumeroTicket + 1;
        tickets.add(ticket);
        if (ticket.getStatut() == StatutTicket.REFUSE) {
            ticket.mettreEnAttente();
            obtenirFile(trajet.getNumero()).offer(ticket);
        }
        return ticket;
    }

    public List<Ticket> annuler(int numeroTicket) throws ElementIntrouvableException, AnnulationImpossibleException {
        Ticket ticket = chercherTicket(numeroTicket);
        ticket.annuler();
        Trajet trajet = ticket.getTrajet();
        Queue<Ticket> file = filesAttente.get(trajet.getNumero());
        if (file == null) {
            return new ArrayList<>();
        }
        file.remove(ticket);
        return promouvoirEnAttente(trajet, file);
    }

    public int positionDansFile(int numeroTicket) throws ElementIntrouvableException {
        Ticket ticket = chercherTicket(numeroTicket);
        Queue<Ticket> file = filesAttente.get(ticket.getTrajet().getNumero());
        if (file == null || ticket.getStatut() != StatutTicket.EN_ATTENTE) {
            return 0;
        }
        int position = 1;
        for (Ticket enAttente : file) {
            if (enAttente.equals(ticket)) {
                return position;
            }
            position = position + 1;
        }
        return 0;
    }

    public int validerDepart(int numeroTrajet) throws ElementIntrouvableException {
        Trajet trajet = trajets.trouver(numeroTrajet);
        int valides = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getTrajet().equals(trajet) && ticket.getStatut() == StatutTicket.RESERVE) {
                ticket.validerAuto();
                valides = valides + 1;
            }
        }
        return valides;
    }

    public List<Ticket> ticketsDuVoyageur(int numeroVoyageur) throws ElementIntrouvableException {
        Voyageur voyageur = voyageurs.trouver(numeroVoyageur);
        List<Ticket> resultat = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getVoyageur().equals(voyageur)) {
                resultat.add(ticket);
            }
        }
        return resultat;
    }

    public Set<Voyageur> voyageursDuTrajet(int numeroTrajet) throws ElementIntrouvableException {
        Trajet trajet = trajets.trouver(numeroTrajet);
        Set<Voyageur> resultat = new HashSet<>();
        for (Ticket ticket : tickets) {
            if (ticket.getTrajet().equals(trajet) && ticket.estDejaReserve()) {
                resultat.add(ticket.getVoyageur());
            }
        }
        return resultat;
    }

    public Set<Voyageur> voyageursCommuns(int numeroTrajetA, int numeroTrajetB) throws ElementIntrouvableException {
        Set<Voyageur> communs = voyageursDuTrajet(numeroTrajetA);
        communs.retainAll(voyageursDuTrajet(numeroTrajetB));
        return communs;
    }

    public List<Trajet> trajetsDisponibles() {
        List<Trajet> resultat = new ArrayList<>();
        for (Trajet trajet : trajets.tous()) {
            if (!trajet.estComplet()) {
                resultat.add(trajet);
            }
        }
        Collections.sort(resultat);
        return resultat;
    }

    public List<Trajet> rechercherTrajets(String villeDepart, String villeArrivee) {
        List<Trajet> resultat = new ArrayList<>();
        for (Trajet trajet : trajets.tous()) {
            if (trajet.getVilleDepart().equalsIgnoreCase(villeDepart)
                    && trajet.getVilleArrivee().equalsIgnoreCase(villeArrivee)) {
                resultat.add(trajet);
            }
        }
        Collections.sort(resultat);
        return resultat;
    }

    public Set<String> villesDesservies() {
        Set<String> villes = new TreeSet<>();
        for (Trajet trajet : trajets.tous()) {
            villes.add(trajet.getVilleArrivee());
        }
        return villes;
    }

    public List<Voyageur> voyageursTries(Comparator<Voyageur> comparateur) {
        List<Voyageur> resultat = voyageurs.tous();
        Collections.sort(resultat, comparateur);
        return resultat;
    }

    public Map<StatutTicket, Integer> compterParStatut() {
        Map<StatutTicket, Integer> compteurs = new TreeMap<>();
        for (StatutTicket statut : StatutTicket.values()) {
            compteurs.put(statut, 0);
        }
        for (Ticket ticket : tickets) {
            compteurs.put(ticket.getStatut(), compteurs.get(ticket.getStatut()) + 1);
        }
        return compteurs;
    }

    public double chiffreAffaires() {
        double total = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getStatut() == StatutTicket.PAYE) {
                total = total + ticket.getPrix();
            }
        }
        return total;
    }

    public int tailleFileAttente(int numeroTrajet) {
        Queue<Ticket> file = filesAttente.get(numeroTrajet);
        if (file == null) {
            return 0;
        }
        return file.size();
    }

    public Ticket chercherTicket(int numeroTicket) throws ElementIntrouvableException {
        for (Ticket ticket : tickets) {
            if (ticket.getNumero() == numeroTicket) {
                return ticket;
            }
        }
        throw new ElementIntrouvableException("Ticket numero " + numeroTicket + " est introuvable.");
    }

    public Voyageur chercherVoyageur(int numeroVoyageur) throws ElementIntrouvableException {
        return voyageurs.trouver(numeroVoyageur);
    }

    public Trajet chercherTrajet(int numeroTrajet) throws ElementIntrouvableException {
        return trajets.trouver(numeroTrajet);
    }

    public List<Ticket> getTickets() {
        return new ArrayList<>(tickets);
    }

    public String getNom() {
        return nom;
    }

    public int nombreVoyageurs() {
        return voyageurs.taille();
    }

    public int nombreTrajets() {
        return trajets.taille();
    }

    public int nombreTickets() {
        return tickets.size();
    }

    @Override
    public void afficher() {
        System.out.println(nom + " : " + voyageurs.taille() + " voyageur(s), "
                + trajets.taille() + " trajet(s), " + tickets.size() + " ticket(s).");
    }

    private boolean aDejaUnTicketActif(Voyageur voyageur, Trajet trajet) {
        for (Ticket ticket : tickets) {
            if (ticket.getVoyageur().equals(voyageur) && ticket.getTrajet().equals(trajet) && ticket.estActif()) {
                return true;
            }
        }
        return false;
    }

    private Queue<Ticket> obtenirFile(int numeroTrajet) {
        LinkedList<Ticket> file = filesAttente.get(numeroTrajet);
        if (file == null) {
            file = new LinkedList<>();
            filesAttente.put(numeroTrajet, file);
        }
        return file;
    }

    private List<Ticket> promouvoirEnAttente(Trajet trajet, Queue<Ticket> file) {
        List<Ticket> promus = new ArrayList<>();
        while (!file.isEmpty() && !trajet.estComplet()) {
            Ticket suivant = file.poll();
            if (suivant.reessayerReservation()) {
                promus.add(suivant);
            }
        }
        return promus;
    }
}
