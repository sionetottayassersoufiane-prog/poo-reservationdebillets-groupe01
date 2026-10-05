package billetterie.modele;

import billetterie.contrats.Affichable;
import java.io.Serializable;
import java.util.Objects;

public class Voyageur implements Affichable, Comparable<Voyageur>, Serializable {
    private static final long serialVersionUID = 1L;
    private final int numero;
    private final String nom;
    private final String email;

    public Voyageur(int numero, String nom, String email) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom du voyageur ne peut pas etre vide.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("L'email du voyageur doit etre une adresse valide.");
        }
        this.numero = numero;
        this.nom = nom;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voyageur)) return false;
        Voyageur autre = (Voyageur) o;
        return numero == autre.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public int compareTo(Voyageur autre) {
        return Integer.compare(numero, autre.numero);
    }

    @Override
    public String toString() {
        return "Voyageur " + numero + " - " + nom + " (" + email + ")";
    }

    @Override
    public void afficher() {
        System.out.println(this);
    }

    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }
}
