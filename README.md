# poo-reservationdebillets-groupe01
# Billetterie de voyage — POO IIAA511

**Enseignant :** Dr Babacar LEYE
**Groupe :** SIONE Totta Yasser Soufiane, YELKUNI Tindwendé Franck Onel, NIKIEMA P. Hanifah, NADEMBEGA Ingrid Océane

## Contexte et description du système

On est partis d'un besoin assez simple : une petite agence de transport veut un programme pour suivre ses trajets et ses voyageurs. 

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

```
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
```

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

### SIONE Totta Yasser Soufiane : modèle, hiérarchie et interface (`Trajet`, `TrajetBus`, `TrajetVol`, `Reservable`)

J'ai géré la partie centrale du programme : la classe `Trajet` et ses deux sous-classes. Au TD-TP1, on avait mis le délai d'annulation en dur dans `Ticket`, à 72h pour tout le monde. Mais en vrai, un bus et un avion ça marche pas pareil : un vol se bloque bien plus tôt qu'un bus. Du coup quand on est passés à l'héritage, j'ai sorti cette règle de `Ticket` pour en faire une méthode abstraite, `delaiAnnulationHeures()`, que chaque sous-classe redéfinit comme elle veut : 24h pour `TrajetBus`, 72h pour `TrajetVol`. Comme ça, `Ticket` n'a même plus besoin de savoir si c'est un bus ou un avion en face, il demande juste au trajet.

Après, on nous a demandé de sortir `reserverPlace()`, `libererPlace()` et `estComplet()` dans une vraie interface au lieu de les laisser dans la classe abstraite. J'ai fait `Reservable` pour ça, et `Trajet` l'implémente avec `@Override` sur les trois méthodes. Ça change rien au fonctionnement, mais ça sépare bien ce que `Trajet` *est* de ce qu'il *sait faire*.

Sinon je gère pas de vraies dates, on n'a pas encore vu ça en cours et ça nous aurait fait perdre trop de temps pour pas grand-chose. J'ai juste mis un compteur, `heuresAvantDepart`, fixé à la création du trajet. C'est un raccourci mais ça suffit largement pour tester les règles d'annulation.

### YELKUNI Tindwendé Franck Onel : la classe `Ticket`

Moi c'est tout ce qui touche à la réservation. `Ticket` fait le lien entre un voyageur et un trajet, et garde son statut : Réservé à la création, Payé une fois validé, ou Annulé si c'est encore possible. Le truc important c'est que `Ticket` doit jamais avoir à deviner le type du trajet pour savoir s'il peut annuler. Il appelle juste `delaiAnnulationHeures()` sur le trajet et c'est tout. Ça m'évite d'écrire des trucs genre "si c'est un bus... si c'est un vol...", ce qui aurait cassé tout l'intérêt de ce que Yasser a fait avec l'héritage et l'interface.

Le bug le plus chiant que j'ai eu à régler, c'était sur la création d'un ticket : le constructeur appelait `trajet.reserverPlace()` sans regarder ce que ça renvoyait, du coup un ticket pouvait se retrouver "Réservé" même si le trajet était complet et qu'aucune place avait vraiment été prise. On avait bricolé ça dans `Main` en vérifiant `estComplet()` avant, mais c'était pas la bonne classe pour gérer ça. Je l'ai corrigé direct dans `Ticket` : le constructeur regarde maintenant ce que `reserverPlace()` renvoie, et si ça échoue, le ticket naît "Refusé" au lieu de raconter n'importe quoi sur son état. C'est exactement ce bug qu'on nous a signalé, donc c'était la priorité pour cette version.

Avec Ingrid, on a aussi repris `annuler()` et `validerAuto()` pour être sûrs qu'aucune des deux méthodes touche au statut ou aux places quand l'opération est refusée, même sur un ticket déjà annulé ou déjà refusé.

### NIKIEMA P. Hanifah : la classe `Voyageur` et le schéma UML

Ma partie est plus courte côté code mais bon quand même : la classe `Voyageur`, avec numéro, nom et email, tout en privé, tous initialisés par un seul constructeur. Je me suis aussi occupée du diagramme de classes, avec les liens entre `Voyageur`, `Trajet` et `Ticket`, l'héritage entre `Trajet`, `TrajetBus` et `TrajetVol`, et maintenant l'implémentation de `Reservable` par `Trajet`.

Ce que j'ai dû défendre en groupe, c'est de pas mettre de setters pour le nom et l'email. Une fois qu'un voyageur est créé, rien ne change dans cette version du programme, donc des setters auraient juste ouvert une porte inutile pour modifier les données n'importe comment depuis `Main`, exactement ce qu'on essaie d'éviter avec l'encapsulation.

Ma vraie difficulté, c'était de pas trop surcharger le diagramme UML, et bien distinguer visuellement l'héritage (trait plein) de l'implémentation d'interface (trait pointillé), un truc qui existait pas avant qu'on ajoute `Reservable`.

### NADEMBEGA Ingrid Océane : la classe `Main` et les tests de scénarios

Moi je me suis occupée de faire tourner tout ce que les autres ont codé. Dans `Main`, je crée deux voyageurs et deux trajets, un bus et un vol, je réserve une place sur chacun, puis je teste les deux cas d'annulation : celui qui doit marcher (le bus, largement à l'avance) et celui qui doit être refusé (le vol, trop proche du départ). J'ai ajouté en plus le test de validation automatique, la vérification anti-double-réservation, et un dernier scénario avec un trajet créé direct à zéro place.

J'avais remarqué que mon `Main` de départ cachait un bug : je vérifiais `estComplet()` avant de créer un ticket, mais si c'était faux, la variable `ticket` restait à `null` et ça plantait au premier `afficher()`. Une fois que Franck a corrigé `Ticket` pour qu'il gère ça lui-même, j'ai pu simplifier `Main` en enlevant complètement ce bricolage.

La nouveauté de cette version, c'est `TestsScenarios`, qu'on nous a demandé d'ajouter pour vérifier chaque cas clairement au lieu de juste raconter un scénario dans `Main`. J'ai repris chaque exigence une par une, et surtout le point sur lequel on a le plus insisté dans le retour : une opération interdite (annuler un ticket déjà annulé, valider un ticket déjà annulé, annuler un ticket né refusé) ne doit jamais changer l'état. Pour chaque cas, je compare l'état avant et après l'opération refusée, plutôt que de me fier juste au message affiché.
