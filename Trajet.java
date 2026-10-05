package billetterie.modele;

import billetterie.contrats.Affichable;
import billetterie.contrats.Occupable;
import billetterie.exceptions.TrajetCompletException;
import java.io.Serializable;
import java.util.Objects;

public abstract class Trajet implements Occupable, Affichable, Comparable<Trajet>, Serializable {
    private static final long serialVersionUID = 1L;
    private final int numero;
    private final String villeDepart;
    private final String villeArrivee;
    private final String date;
    private final String heureDepart;
    private int placesDisponibles;
    private final int heuresAvantDepart;

    public Trajet(int numero, String villeDepart, String villeArrivee, String date,
                  String heureDepart, int placesDisponibles, int heuresAvantDepart) {
        if (placesDisponibles < 0) {
            throw new IllegalArgumentException("Le nombre de places disponibles ne peut pas etre negatif.");
        }
        if (villeDepart == null || villeDepart.isBlank() || villeArrivee == null || villeArrivee.isBlank()) {
            throw new IllegalArgumentException("Les villes de depart et d'arrivee sont obligatoires.");
        }
        if (villeDepart.equalsIgnoreCase(villeArrivee)) {
            throw new IllegalArgumentException("La ville de depart et la ville d'arrivee doivent etre differentes.");
        }
        if (heuresAvantDepart < 0) {
            throw new IllegalArgumentException("Le delai avant le depart ne peut pas etre negatif.");
        }
        this.numero = numero;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.date = date;
        this.heureDepart = heureDepart;
        this.placesDisponibles = placesDisponibles;
        this.heuresAvantDepart = heuresAvantDepart;
    }

    @Override
    public void reserverPlace() throws TrajetCompletException {
        if (placesDisponibles <= 0) {
            throw new TrajetCompletException("Reservation refusee : le trajet " + numero + " est complet.");
        }
        placesDisponibles = placesDisponibles - 1;
    }

    @Override
    public void libererPlace() {
        placesDisponibles = placesDisponibles + 1;
    }

    @Override
    public boolean estComplet() {
        return placesDisponibles == 0;
    }

    public abstract int delaiAnnulationHeures();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trajet)) return false;
        Trajet autre = (Trajet) o;
        return numero == autre.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public int compareTo(Trajet autre) {
        int resultat = date.compareTo(autre.date);
        if (resultat != 0) {
            return resultat;
        }
        resultat = heureDepart.compareTo(autre.heureDepart);
        if (resultat != 0) {
            return resultat;
        }
        return Integer.compare(numero, autre.numero);
    }

    @Override
    public String toString() {
        return "Trajet " + numero + " : " + villeDepart + " -> " + villeArrivee
                + " le " + date + " a " + heureDepart + " (" + placesDisponibles
                + " place(s) disponible(s), depart dans " + heuresAvantDepart + "h)";
    }

    @Override
    public void afficher() {
        System.out.println(this);
    }

    public int getNumero() {
        return numero;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public String getDate() {
        return date;
    }

    public String getHeureDepart() {
        return heureDepart;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public int getHeuresAvantDepart() {
        return heuresAvantDepart;
    }
}
