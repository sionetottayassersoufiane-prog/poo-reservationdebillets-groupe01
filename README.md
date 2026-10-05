# poo-reservationdebillets-groupe01
# Billetterie de voyage — POO IIAA511

**Enseignant :** Dr Babacar LEYE
**Groupe :** SIONE Totta Yasser Soufiane, YELKUNI Tindwendé Franck Onel, NIKIEMA P. Hanifah, NADEMBEGA Ingrid Océane

## Contexte

Une agence de transport veut suivre ses trajets et ses voyageurs au départ de Ouagadougou. L'agence propose trois façons de voyager : en bus, en train ou en avion. Un voyageur réserve une place sur un trajet, ce qui crée un ticket. Le ticket peut être annulé tant que le délai le permet — 24h avant le départ pour un bus, 48h pour un train, 72h pour un vol, car les compagnies aériennes bloquent les places plus tôt. Si le trajet est complet, le voyageur entre dans une file d'attente et reçoit la première place libérée. Une fois le départ atteint sans annulation, le ticket est validé automatiquement.

Pas de dates réelles ni de paiement en ligne dans cette version. Les données vivent en mémoire pendant l'exécution, et l'agence peut être sauvegardée dans un fichier puis rechargée.

## Le besoin en deux phrases

Un voyageur doit pouvoir réserver une place sur un trajet en bus, en train ou en avion, puis l'annuler tant que le délai le permet. Le programme doit aussi valider un ticket automatiquement au départ, mettre en file d'attente une réservation sur un trajet complet, et ne jamais modifier l'état d'un ticket ou d'un trajet lors d'une opération refusée.

## Les classes du projet

- **Voyageur** — numéro, nom, email ; entièrement immuable après création. Un email ne peut appartenir qu'à un seul voyageur.
- **Trajet** (abstraite) — villes de départ et d'arrivée, date, heure, places disponibles ; déclare `delaiAnnulationHeures()` en abstrait.
- **TrajetBus** / **TrajetTrain** / **TrajetVol** — héritent de `Trajet`, chacun avec son propre délai d'annulation (24h / 48h / 72h).
- **Affichable** et **Occupable** (interfaces) — contrats d'affichage et de gestion des places.
- **Ticket** — relie un voyageur à un trajet, porte le statut (`StatutTicket`) et le fait évoluer sans jamais tester le type réel du trajet : il interroge simplement `delaiAnnulationHeures()`.
- **Agence** — réserve, annule, gère la file d'attente et les statistiques ; **Depot** (générique) range les voyageurs et les trajets ; **ComparateurVoyageurParNom** trie les voyageurs.
- **Sauvegarde** — enregistre et recharge l'agence par sérialisation, en passant par un fichier temporaire.
- **Exceptions** — `BilletterieException` et ses six filles (trajet complet, annulation impossible, doublon, réservation invalide, élément introuvable, sauvegarde).
- **Main** — démonstration de bout en bout, ou menu interactif avec l'argument `menu`.
- **MenuConsole** — menu en console qui gère les saisies invalides sans s'arrêter.
- **TestsScenarios** — 22 scénarios (182 vérifications) comparant l'état avant et après chaque opération.

## Structure du dépôt

```
├── src/billetterie/
│   ├── contrats/     Affichable, Occupable
│   ├── modele/       Voyageur, Trajet, TrajetBus, TrajetTrain, TrajetVol, Ticket, StatutTicket
│   ├── exceptions/   BilletterieException et ses filles
│   ├── service/      Agence, Depot, ComparateurVoyageurParNom, Sauvegarde
│   ├── ui/           Main, MenuConsole
│   └── tests/        TestsScenarios
├── test/             tests JUnit 5 (109 tests, 8 classes)
├── scripts/          compiler-et-tester et lancer-demonstration (.sh et .bat)
├── docs/             cahier des charges, rapport, diagramme de classes (PDF + LaTeX)
└── README.md
```

## Comment lancer le programme

Java 11 ou plus. Les scripts du dossier `scripts` compilent et lancent tout :

```
sh scripts/compiler-et-tester.sh          (Windows : scripts\compiler-et-tester.bat)
sh scripts/lancer-demonstration.sh        (démonstration)
sh scripts/lancer-demonstration.sh menu   (menu interactif)
```

Dans IntelliJ IDEA : marquer `src` comme Sources Root et `test` comme Test Sources Root, exécuter `Main` ou `TestsScenarios` (chaque vérification affiche `OK` ou `ECHEC`, avec un bilan à la fin), puis clic droit sur `test` → Run All Tests pour JUnit 5.

## Deux choix à défendre à l'oral

1. **Exception plutôt que booléen pour l'annulation** — une annulation refusée lève une exception vérifiée au lieu de renvoyer un booléen. On voulait forcer l'appelant à gérer explicitement le refus (via `try/catch`) plutôt que de laisser un booléen ignoré passer inaperçu.
2. **Modèle et service muets** — ils n'écrivent plus rien à l'écran : l'annulation renvoie la liste des tickets promus depuis la file d'attente, et seule l'interface (`Main`, `MenuConsole`) affiche. La logique métier reste ainsi testable et indépendante de la console.

## Ce qui a changé depuis la dernière version

- Ajout du train, par une simple classe fille de `Trajet` sans toucher au reste du code.
- Ajout de la file d'attente : un trajet complet n'est plus un simple refus, la place libérée va au premier de la file.
- Ajout de la sauvegarde et du rechargement de l'agence, avec une exception dédiée.
- Ajout du menu interactif, de `Agence`, `Depot` et des règles d'unicité (email, ticket actif par trajet).
- L'interface `Reservable` est remplacée par `Affichable` et `Occupable`.
- Tests étendus : 22 scénarios et 109 tests JUnit 5.
- Ajout des scripts de lancement, du cahier des charges, du rapport et du diagramme de classes.

## Répartition du travail

- **SIONE Totta Yasser Soufiane** — hiérarchie `Trajet`, `TrajetBus`, `TrajetVol`, délai d'annulation par type de trajet.
- **YELKUNI Tindwendé Franck Onel** — `Ticket` : statut, transitions, réservation sur trajet complet.
- **NIKIEMA P. Hanifah** — `Voyageur` et diagramme de classes (héritage en trait plein, implémentation en trait pointillé).
- **NADEMBEGA Ingrid Océane** — `Main` et `TestsScenarios` : scénarios de démonstration et vérification de chaque cas exigé.
- **Version finale** — train, file d'attente, sauvegarde, menu, tests JUnit et documents réalisés en commun par le groupe.
