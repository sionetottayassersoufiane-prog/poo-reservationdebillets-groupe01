public class Voyageur {
    private int numero;
    private String nom;
    private String email;

    public Voyageur(int numero, String nom, String email) {
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
