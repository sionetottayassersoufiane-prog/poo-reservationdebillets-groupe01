package billetterie.service;

import billetterie.exceptions.DoublonException;
import billetterie.exceptions.ElementIntrouvableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Depot<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nomElement;
    private final TreeMap<Integer, T> elements;

    public Depot(String nomElement) {
        this.nomElement = nomElement;
        this.elements = new TreeMap<>();
    }

    public void ajouter(int cle, T element) throws DoublonException {
        if (element == null) {
            throw new IllegalArgumentException("Impossible d'ajouter un element nul.");
        }
        if (elements.containsKey(cle)) {
            throw new DoublonException(nomElement + " numero " + cle + " existe deja.");
        }
        elements.put(cle, element);
    }

    public T trouver(int cle) throws ElementIntrouvableException {
        T element = elements.get(cle);
        if (element == null) {
            throw new ElementIntrouvableException(nomElement + " numero " + cle + " est introuvable.");
        }
        return element;
    }

    public boolean contient(int cle) {
        return elements.containsKey(cle);
    }

    public List<T> tous() {
        return new ArrayList<>(elements.values());
    }

    public int taille() {
        return elements.size();
    }
}
