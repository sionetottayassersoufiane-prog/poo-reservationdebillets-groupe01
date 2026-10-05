package billetterie.service;

import billetterie.modele.Voyageur;
import java.util.Comparator;

public class ComparateurVoyageurParNom implements Comparator<Voyageur> {
    @Override
    public int compare(Voyageur premier, Voyageur second) {
        int resultat = premier.getNom().compareToIgnoreCase(second.getNom());
        if (resultat != 0) {
            return resultat;
        }
        return Integer.compare(premier.getNumero(), second.getNumero());
    }
}
