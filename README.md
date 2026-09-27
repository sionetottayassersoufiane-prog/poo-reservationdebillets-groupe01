# poo-reservationdebillets-groupe01
# Billetterie de voyage — POO IIAA511

**Enseignant :** Dr Babacar LEYE
**Groupe :** SIONE Totta Yasser Soufiane, YELKUNI Tindwendé Franck Onel, NIKIEMA P. Hanifah, NADEMBEGA Ingrid Océane

## Contexte

Une agence de transport veut suivre ses trajets et ses voyageurs. L'agence propose deux façons de voyager, en bus ou en avion. Un voyageur réserve une place sur un trajet, ce qui crée un ticket. Le ticket peut être annulé tant que le délai le permet — 24h avant le départ pour un bus, 72h pour un vol, car les compagnies aériennes bloquent les places plus tôt. Une fois le départ atteint sans annulation, le ticket est validé automatiquement.
Pas de dates réelles, pas d'historique, pas de paiement en ligne dans cette version : toutes les données restent en mémoire pendant l'exécution.

## Le besoin en deux phrases

Un voyageur doit pouvoir réserver une place sur un trajet en bus ou en avion, puis l'annuler tant que le délai le permet. Le programme doit aussi valider un ticket automatiquement au départ, refuser une réservation sur un trajet complet, et ne jamais modifier l'état d'un ticket ou d'un trajet lors d'une opération refusée.

## Les classes du projet

- **Voyageur** — numéro, nom, email ; entièrement immuable après création.
- **Trajet** (abstraite) — ville de départ/arrivée, date, heure, places disponibles ; implémente `Reservable` et déclare `delaiAnnulationHeures()` en abstrait.
- **Reservable** (interface) — capacité de réservation d'un trajet : `reserverPlace()`, `libererPlace()`, `estComplet()`.
- **TrajetBus** / **TrajetVol** — héritent de `Trajet`, chacun avec sa spécificité (compagnie / n° de vol) et son propre délai d'annulation (24h / 72h).
- **Ticket** — relie un voyageur à un trajet, porte le statut (`RESERVE`, `PAYE`, `ANNULE`, `REFUSE`) et le fait évoluer sans jamais tester le type réel du trajet : il interroge simplement `delaiAnnulationHeures()`.
- **Main** — scénario de démonstration de bout en bout.
- **TestsScenarios** — vérifie chaque cas demandé (réservation acceptée, trajet complet, annulation acceptée/refusée, opérations interdites sans effet, fiabilité multi-voyageurs, cas limites) en comparant l'état avant/après chaque opération.

## Structure du dépôt

```
├── Voyageur.java
├── Trajet.java
├── TrajetBus.java
├── TrajetVol.java
├── Reservable.java
├── Ticket.java
├── StatutTicket.java
├── TrajetCompletException.java
├── AnnulationImpossibleException.java
├── Main.java
├── TestsScenarios.java
├── diagramme-classes.pdf
└── README.md
```

## Comment lancer le programme

1. Ouvrir le projet dans IntelliJ IDEA (ou un autre IDE Java).
2. Compiler l'ensemble des fichiers.
3. Exécuter `Main` pour le scénario complet, ou `TestsScenarios` pour vérifier chaque cas demandé (chaque test affiche `[OK]` ou `[ECHEC]`, avec un bilan à la fin).

## Deux choix à défendre à l'oral

1. **Exception plutôt que booléen pour `annuler()`** — `Ticket.annuler()` lève une `AnnulationImpossibleException` au lieu de renvoyer un booléen. On voulait forcer l'appelant à gérer explicitement le refus (via `try/catch`) plutôt que de laisser un booléen ignoré passer inaperçu.
2. **Le constructeur de `Ticket` gère lui-même l'échec de réservation** — il appelle `trajet.reserverPlace()` et, si le trajet est complet, fait naître le ticket avec le statut `REFUSE` au lieu de laisser `Main` vérifier `estComplet()` avant chaque création. La classe `Ticket` porte ainsi seule la responsabilité de son propre état.

## Ce qui a changé depuis la dernière version

- Ajout de l'interface `Reservable`, implémentée par `Trajet`, et utilisée de façon polymorphe dans `TestsScenarios`.
- Correction d'un bug dans `Ticket` : le constructeur ignorait le résultat de `reserverPlace()`, un ticket pouvait donc naître « Réservé » sur un trajet déjà complet. Il naît désormais « Refusé » dans ce cas.
- Ajout de `TestsScenarios.java`, qui vérifie explicitement les cas listés dans le retour du professeur, notamment qu'aucune opération refusée ne modifie l'état.
- Harmonisation des noms des membres dans les fichiers du dépôt.

## Répartition du travail

- **SIONE Totta Yasser Soufiane** — `Trajet`, `TrajetBus`, `TrajetVol`, `Reservable` : hiérarchie, délai d'annulation par type de trajet, extraction de l'interface.
- **YELKUNI Tindwendé Franck Onel** — `Ticket` : statut, transitions, correction du bug de réservation sur trajet complet.
- **NIKIEMA P. Hanifah** — `Voyageur` et diagramme de classes (héritage en trait plein, implémentation en trait pointillé).
- **NADEMBEGA Ingrid Océane** — `Main` et `TestsScenarios` : scénarios de démonstration et vérification de chaque cas exigé.
