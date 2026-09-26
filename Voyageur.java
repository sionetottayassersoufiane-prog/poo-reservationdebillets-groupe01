public class Voyageur {
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

    public void afficher() {
        System.out.println("Voyageur " + numero + " - " + nom + " (" + email + ")");
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
