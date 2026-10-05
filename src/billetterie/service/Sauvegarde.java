package billetterie.service;

import billetterie.exceptions.SauvegardeException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class Sauvegarde {

    private Sauvegarde() {
    }

    public static void sauvegarder(Agence agence, String chemin) throws SauvegardeException {
        if (agence == null) {
            throw new IllegalArgumentException("L'agence a sauvegarder ne peut pas etre nulle.");
        }
        if (chemin == null || chemin.isBlank()) {
            throw new IllegalArgumentException("Le chemin du fichier ne peut pas etre vide.");
        }
        Path destination = Paths.get(chemin);
        Path temporaire = Paths.get(chemin + ".tmp");
        try (ObjectOutputStream sortie = new ObjectOutputStream(Files.newOutputStream(temporaire))) {
            sortie.writeObject(agence);
        } catch (IOException e) {
            supprimerSansErreur(temporaire);
            throw new SauvegardeException("Sauvegarde impossible dans " + chemin + " : " + e.getMessage());
        }
        try {
            Files.move(temporaire, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            supprimerSansErreur(temporaire);
            throw new SauvegardeException("Sauvegarde impossible dans " + chemin + " : " + e.getMessage());
        }
    }

    public static Agence charger(String chemin) throws SauvegardeException {
        if (chemin == null || chemin.isBlank()) {
            throw new IllegalArgumentException("Le chemin du fichier ne peut pas etre vide.");
        }
        try (ObjectInputStream entree = new ObjectInputStream(Files.newInputStream(Paths.get(chemin)))) {
            return (Agence) entree.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new SauvegardeException("Chargement impossible depuis " + chemin + " : " + e.getMessage());
        }
    }

    private static void supprimerSansErreur(Path fichier) {
        try {
            Files.deleteIfExists(fichier);
        } catch (IOException e) {
            System.err.println("Fichier temporaire non supprime : " + fichier);
        }
    }
}
