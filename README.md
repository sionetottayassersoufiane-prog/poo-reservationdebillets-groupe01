# poo-reservationdebillets-groupe01
# Billetterie de voyage — POO IIAA511

**Enseignant :** Dr Babacar LEYE
**Groupe :** SIONE Totta Yasser Soufiane, YELKUNI Tindwendé Franck Onel, NIKIEMA P. Hanifah, NADEMBEGA Ingrid Océane

## Contexte et description du système

On est partis d'un besoin assez simple : une petite agence de transport veut un programme pour suivre ses trajets et ses voyageurs. Pas de base de données, pas d'interface graphique, rien de compliqué, juste de quoi mettre en pratique ce qu'on a vu sur les classes et l'encapsulation.

L'agence propose deux façons de voyager, en bus ou en avion. Chaque trajet a un numéro, une ville de départ, une ville d'arrivée, une date, une heure et un nombre de places encore libres. Un voyageur, lui, c'est juste un numéro, un nom et un email. Quand il réserve une place, ça crée un ticket. On peut annuler ce ticket tant qu'on n'est pas trop proche du départ, sinon c'est refusé. Et une fois le départ arrivé, le ticket passe automatiquement en "payé".

Ce qui nous a plu dans ce sujet, c'est que le bus et l'avion n'ont pas les mêmes règles : un bus, on peut l'annuler jusqu'à 24h avant, un vol seulement jusqu'à 72h avant, parce que les compagnies aériennes bloquent les places bien plus tôt. Au TD-TP1, on n'avait pas encore ce recul-là, du coup on avait mis 72h partout, pour tous les trajets sans distinction. C'est en passant à l'héritage au TD-TP2 qu'on a pu corriger ça, en laissant chaque type de trajet fixer sa propre règle. On ne gère toujours pas les vraies dates, ni l'historique des réservations, ni un quelconque paiement en ligne : ce n'était pas demandé, et on n'a de toute façon pas encore vu comment faire ça proprement.

## Le besoin en deux phrases

Un voyageur doit pouvoir réserver une place sur un trajet en bus ou en avion, puis l'annuler tant que le délai le permet. Le programme doit aussi valider un ticket automatiquement au départ, refuser une réservation sur un trajet complet et empêcher qu'un même ticket soit réservé deux fois.

## Les classes du projet

- **Voyageur** : une personne qui réserve un trajet (numéro, nom, email).
- **Trajet** (abstraite) : tout ce qui est commun à un déplacement, villes, date, heure, places disponibles. Elle oblige ses classes filles à préciser leur propre délai d'annulation, et on ne peut jamais l'instancier telle quelle.
- **Reservable** (interface) : ce que sait faire un trajet qu'on peut réserver puis libérer (`reserverPlace()`, `libererPlace()`, `estComplet()`). `Trajet` l'implémente.
- **TrajetBus** et **TrajetVol** : héritent de `Trajet`, chacune avec sa particularité (la compagnie pour le bus, le numéro de vol pour l'avion) et sa propre règle d'annulation.
- **Ticket** : fait le lien entre un voyageur et un trajet, suit son statut (Réservé, Payé, Annulé, ou Refusé si le trajet était déjà complet) et bloque les doubles réservations.
- **Main** : rejoue tout ça avec des exemples concrets, du début à la fin.
- **TestsScenarios** : vérifie un par un les points qu'on nous a demandé de couvrir, réservation acceptée, trajet complet, annulation acceptée, annulation refusée, et surtout qu'aucune opération interdite ne modifie l'état d'un ticket ou d'un trajet.

## Structure du dépôt

├── src/
│   ├── Reservable.java
│   ├── Trajet.java
│   ├── TrajetBus.java
│   └── TrajetVol.java
├── Voyageur.java
├── Ticket.java
├── Main.java
├── TestsScenarios.java
├── diagramme-classes-page1.pdf
└── README.md

## Comment lancer le programme

1. Ouvrir le projet dans IntelliJ IDEA (ou un autre IDE Java).
2. Compiler l'ensemble des fichiers.
3. Exécuter la classe `Main` pour le scénario complet, ou `TestsScenarios` pour vérifier chaque cas demandé (chaque test affiche `[OK]` ou `[ECHEC]`, avec un bilan à la fin).

Le programme affiche dans l'ordre : la création de deux voyageurs et deux trajets, une réservation sur le bus suivie d'une annulation acceptée, une réservation sur le vol suivie d'une annulation refusée, la validation automatique d'un ticket, la vérification anti-double-réservation, puis un dernier cas où le trajet est déjà complet à la création.

---

## Ce qui a changé depuis la dernière version

- Ajout de l'interface `Reservable`, implémentée par `Trajet`.
- Correction d'un bug dans `Ticket` : le constructeur ignorait le résultat de `reserverPlace()`, un ticket pouvait donc naître "Réservé" sur un trajet déjà complet. Il naît maintenant "Refusé" dans ce cas, ce qui a aussi permis de simplifier `Main`, qui contournait ce problème avec une vérification `estComplet()` avant chaque création de ticket.
- Ajout de `TestsScenarios.java`, qui vérifie explicitement les cas demandés dans le retour du prof.
- Noms des membres harmonisés dans tous les fichiers.

## Ce que chacun a fait

### SIONE Totta Yasser Soufiane — modèle, hiérarchie et interface (`Trajet`, `TrajetBus`, `TrajetVol`, `Reservable`)

Je me suis chargé de la colonne vertébrale du programme, la classe `Trajet` et ses deux sous-classes. Au TD-TP1, on avait bêtement fixé le délai d'annulation à 72h en dur dans `Ticket`, pareil pour tout le monde. Le problème, c'est qu'un bus et un avion ne fonctionnent juste pas de la même manière : un vol se réserve et se bloque bien plus tôt qu'un bus. Donc au moment de passer à l'héritage, j'ai sorti cette règle de `Ticket` et j'en ai fait une méthode abstraite, `delaiAnnulationHeures()`, que chaque sous-classe redéfinit à sa façon : 24h pour `TrajetBus`, 72h pour `TrajetVol`. Résultat, `Ticket` n'a même plus besoin de savoir si elle a affaire à un bus ou à un avion, elle demande juste au trajet sa propre règle.

Plus récemment, on nous a demandé de sortir `reserverPlace()`, `libererPlace()` et `estComplet()` dans une vraie interface plutôt que de les laisser flotter directement dans la classe abstraite. J'ai créé `Reservable` pour ça, et `Trajet` l'implémente maintenant avec `@Override` sur les trois méthodes. Ça n'a rien changé au comportement du programme, mais ça sépare plus clairement ce que `Trajet` *est* (un état commun à tout trajet) de ce qu'il *sait faire* (être réservé et libéré).

Je n'ai toujours pas géré de vraies dates. On n'a pas encore vu ça en cours, et se lancer là-dedans nous aurait fait perdre un temps fou pour un résultat pas franchement plus lisible. J'ai donc gardé un simple compteur, `heuresAvantDepart`, qu'on fixe à la création du trajet. C'est un raccourci assumé, mais il fait exactement ce qu'il faut pour tester les règles d'annulation.

### YELKUNI Tindwendé Franck Onel — la classe `Ticket`

Ma partie, c'est tout ce qui touche à la réservation en elle-même. `Ticket` fait le lien entre un voyageur et un trajet, et porte son statut : Réservé dès sa création, Payé une fois validé, ou Annulé s'il est encore temps. Le point sur lequel j'ai vraiment insisté, c'est que `Ticket` ne doit jamais avoir à deviner le type du trajet pour savoir s'il peut annuler ou pas. Elle se contente d'appeler `delaiAnnulationHeures()` sur le trajet, et c'est lui qui répond. Ça m'a évité d'écrire un bloc de conditions du style "si c'est un bus, alors... si c'est un vol, alors...", qui aurait cassé tout l'intérêt de ce que Yasser a mis en place avec l'héritage puis l'interface.

Le bug le plus embêtant que j'ai eu à corriger concernait justement la création d'un ticket : le constructeur appelait `trajet.reserverPlace()` sans jamais regarder le résultat, donc un ticket pouvait naître avec le statut "Réservé" même quand le trajet était complet et qu'aucune place n'avait vraiment été prise. On avait "réglé" ça côté `Main` en vérifiant `estComplet()` avant de créer le ticket, mais ce n'était pas la bonne classe pour porter cette responsabilité. J'ai corrigé ça directement dans `Ticket` : le constructeur regarde maintenant ce que répond `reserverPlace()`, et si la réservation échoue, le ticket naît avec le statut "Refusé" au lieu de mentir sur son propre état. C'est ce bug précis qui nous a été signalé dans le retour, donc c'était ma priorité pour cette révision.

J'ai aussi repris `annuler()` et `validerAuto()` avec Ingrid pour être sûrs qu'aucune de ces deux méthodes ne touche au statut ou aux places quand l'opération est refusée, y compris sur un ticket déjà annulé ou déjà refusé.

### NIKIEMA P. Hanifah — la classe `Voyageur` et le schéma UML

Ma partie est plus courte niveau code, mais elle a son importance : la classe `Voyageur`, avec son numéro, son nom et son email, tous en privé, initialisés une bonne fois pour toutes par un seul constructeur. Je me suis aussi occupée du diagramme de classes, en montrant les liens entre `Voyageur`, `Trajet` et `Ticket`, l'héritage entre `Trajet`, `TrajetBus` et `TrajetVol`, et maintenant l'implémentation de `Reservable` par `Trajet`.

Le choix que j'ai dû défendre en groupe, c'est l'absence de setters pour le nom et l'email. Une fois qu'un voyageur est créé, rien ne change plus dans cette version du programme, donc ajouter des setters n'aurait servi qu'à ouvrir une porte inutile pour modifier les données n'importe comment depuis `Main`, ce qu'on cherchait justement à éviter avec l'encapsulation.

Ma difficulté a surtout été de ne pas trop en faire sur le diagramme UML, et de bien distinguer visuellement l'héritage (trait plein) de l'implémentation d'interface (trait pointillé), ce qui n'existait pas encore avant l'arrivée de `Reservable`.

### NADEMBEGA Ingrid Océane — la classe `Main` et les tests de scénarios

Je me suis occupée de faire vivre tout ce que les autres ont codé. Dans `Main`, je crée deux voyageurs et deux trajets, un bus et un vol, je réserve une place sur chacun, puis je teste les deux cas d'annulation : celui qui doit passer (le bus, largement à l'avance) et celui qui doit être refusé (le vol, trop proche du départ). J'ai ajouté en plus le test de validation automatique du ticket, la vérification anti-double-réservation, et un dernier scénario avec un trajet créé directement à zéro place disponible.

J'avais repéré que mon `Main` initial cachait un bug : je vérifiais `estComplet()` avant de créer un ticket, mais si cette condition était fausse, la variable `ticket` restait à `null` et le programme plantait au premier `afficher()`. Une fois que Franck a corrigé `Ticket` pour qu'il gère lui-même ce cas, j'ai pu simplifier `Main` en retirant complètement ce contournement.

La nouveauté de cette version, c'est `TestsScenarios`, qu'on nous a demandé d'ajouter pour vérifier explicitement chaque cas plutôt que de se contenter d'un scénario raconté dans `Main`. J'y ai repris chaque exigence une par une, et surtout le point sur lequel le retour insistait le plus : une opération interdite (annuler un ticket déjà annulé, valider un ticket déjà annulé, annuler un ticket né refusé) ne doit jamais changer l'état. Dans chaque cas, je compare l'état avant et après l'opération refusée, plutôt que de me fier au seul message affiché.
