# poo-reservationdebillets-groupe01
# Billetterie de voyage — POO IIAA511

**Enseignant :** Dr Babacar LEYE
**Groupe :** SIONE Totta Yasser Soufiane, YELKUNI Franck Onel, NIKIEMA P. Hanifah, NADEMBEGA Ingrid

## Contexte et description du système

On est partis d'un besoin assez simple : une petite agence de transport veut un programme pour suivre ses trajets et ses voyageurs. Pas de base de données, pas d'interface graphique, rien de compliqué, juste de quoi mettre en pratique ce qu'on a vu sur les classes et l'encapsulation.

L'agence propose deux façons de voyager, en bus ou en avion. Chaque trajet a un numéro, une ville de départ, une ville d'arrivée, une date, une heure et un nombre de places encore libres. Un voyageur, lui, c'est juste un numéro, un nom et un email. Quand il réserve une place, ça crée un ticket. On peut annuler ce ticket tant qu'on n'est pas trop proche du départ, sinon c'est refusé. Et une fois le départ arrivé, le ticket passe automatiquement en "payé".

Ce qui nous a plu dans ce sujet, c'est que le bus et l'avion n'ont pas les mêmes règles : un bus, on peut l'annuler jusqu'à 24h avant, un vol seulement jusqu'à 72h avant, parce que les compagnies aériennes bloquent les places bien plus tôt. Au TD-TP1, on n'avait pas encore ce recul-là, du coup on avait mis 72h partout, pour tous les trajets sans distinction. C'est en passant à l'héritage au TD-TP2 qu'on a pu corriger ça, en laissant chaque type de trajet fixer sa propre règle. On ne gère toujours pas les vraies dates, ni l'historique des réservations, ni un quelconque paiement en ligne : ce n'était pas demandé, et on n'a de toute façon pas encore vu comment faire ça proprement.

## Le besoin en deux phrases

Un voyageur doit pouvoir réserver une place sur un trajet en bus ou en avion, puis l'annuler tant que le délai le permet. Le programme doit aussi valider un ticket automatiquement au départ et empêcher qu'un même ticket soit réservé deux fois.

## Les classes du projet

- **Voyageur** : une personne qui réserve un trajet (numéro, nom, email).
- **Trajet** (abstraite) : tout ce qui est commun à un déplacement, villes, date, heure, places disponibles. Elle oblige ses classes filles à préciser leur propre délai d'annulation, et on ne peut jamais l'instancier telle quelle.
- **TrajetBus** et **TrajetVol** : héritent de `Trajet`, chacune avec sa particularité (la compagnie pour le bus, le numéro de vol pour l'avion) et sa propre règle d'annulation.
- **Ticket** : fait le lien entre un voyageur et un trajet, suit son statut (Réservé, Payé, Annulé, ou Refusé si le trajet était déjà complet) et bloque les doubles réservations.
- **Main** : rejoue tout ça avec des exemples concrets, du début à la fin.

## Structure du dépôt

├── src/
│   ├── Voyageur.java
│   ├── Trajet.java
│   ├── TrajetBus.java
│   ├── TrajetVol.java
│   ├── Ticket.java
│   └── Main.java
├── docs/
│   └── diagramme-classes.pdf
└── README.md

## Comment lancer le programme

1. Ouvrir le projet dans IntelliJ IDEA (ou un autre IDE Java).
2. Compiler l'ensemble des fichiers du dossier `src/`.
3. Exécuter la classe `Main`.

Le programme affiche dans l'ordre : la création de deux voyageurs et deux trajets, une réservation sur le bus suivie d'une annulation acceptée, une réservation sur le vol suivie d'une annulation refusée, la validation automatique d'un ticket, puis la vérification anti-double-réservation.

---

## Ce que chacun a fait

### SIONE Totta Yasser Soufiane — modèle et hiérarchie (`Trajet`, `TrajetBus`, `TrajetVol`)

Je me suis chargé de la colonne vertébrale du programme, la classe `Trajet` et ses deux sous-classes. Au TD-TP1, on avait bêtement fixé le délai d'annulation à 72h en dur dans `Ticket`, pareil pour tout le monde. Le problème, c'est qu'un bus et un avion ne fonctionnent juste pas de la même manière : un vol se réserve et se bloque bien plus tôt qu'un bus. Donc au moment de passer à l'héritage, j'ai sorti cette règle de `Ticket` et j'en ai fait une méthode abstraite, `delaiAnnulationHeures()`, que chaque sous-classe redéfinit à sa façon : 24h pour `TrajetBus`, 72h pour `TrajetVol`. Résultat, `Ticket` n'a même plus besoin de savoir si elle a affaire à un bus ou à un avion, elle demande juste au trajet sa propre règle.

Deuxième chose que j'ai dû justifier : je n'ai pas géré de vraies dates. On n'a pas encore vu ça en cours, et se lancer là-dedans nous aurait fait perdre un temps fou pour un résultat pas franchement plus lisible. J'ai donc gardé un simple compteur, `heuresAvantDepart`, qu'on fixe à la création du trajet. C'est un raccourci assumé, mais il fait exactement ce qu'il faut pour tester les règles d'annulation.

Là où j'ai vraiment galéré, c'est que dans une version antérieure j'avais recopié la méthode `afficher()` presque à l'identique dans `TrajetBus` et dans `TrajetVol`, avec juste une ligne qui changeait. Ça marchait, mais c'était clairement pas propre, deux copier-coller pour une seule idée. Je suis revenu dessus et j'ai gardé `afficher()` uniquement dans `Trajet`, chaque classe fille l'appelle avec `super.afficher()` puis ajoute seulement sa ligne en plus (la compagnie ou le numéro de vol). C'est un détail, mais c'est le genre d'erreur qu'on ne voit qu'en la refaisant une deuxième fois.

En relisant tout à la fin, j'ai aussi rendu `final` les attributs qui ne bougent jamais après la création (numéro, villes, date...) et j'ai sorti les 24h et les 72h dans des constantes (`DELAI_ANNULATION_HEURES`) plutôt que des nombres écrits en dur dans le code. Ça paraît petit, mais ça évite qu'on change la valeur à un seul endroit et qu'on oublie l'autre.

### YELKUNI Franck Onel — la classe `Ticket`

Ma partie, c'est tout ce qui touche à la réservation en elle-même. `Ticket` fait le lien entre un voyageur et un trajet, et porte son statut : Réservé dès sa création, Payé une fois validé, ou Annulé s'il est encore temps. Le point sur lequel j'ai vraiment insisté, c'est que `Ticket` ne doit jamais avoir à deviner le type du trajet pour savoir s'il peut annuler ou pas. Elle se contente d'appeler `delaiAnnulationHeures()` sur le trajet, et c'est lui qui répond. Ça m'a évité d'écrire un bloc de conditions du style "si c'est un bus, alors... si c'est un vol, alors...", qui aurait cassé tout l'intérêt de ce que Yasser a mis en place avec l'héritage.

J'ai aussi ajouté une méthode `estDejaReserve()`, parce qu'une des questions de revue du sujet demandait explicitement d'empêcher qu'un même document (ou ici, ticket) soit réservé deux fois. Elle renvoie vrai si le statut est Réservé ou Payé, ce qui suffit à couvrir le cas.

Honnêtement, ma vraie difficulté a été de gérer proprement les refus, sans jamais laisser le ticket dans un état bizarre. Si l'annulation échoue, que ce soit parce que le statut n'est pas "Réservé" ou parce que le délai est dépassé, il ne fallait surtout pas toucher au statut ni libérer la place par erreur. J'ai dû reprendre plusieurs fois cette méthode avec Ingrid, en testant différents scénarios dans `Main`, avant d'être sûr que le comportement était le bon dans tous les cas.

Un deuxième bug s'est glissé dans la première version : le constructeur de `Ticket` appelait `trajet.reserverPlace()` sans jamais regarder le résultat, donc un ticket pouvait naître avec le statut "Réservé" même quand le trajet était complet et qu'aucune place n'avait vraiment été prise. On avait "réglé" ça côté `Main` en vérifiant `estComplet()` avant de créer le ticket, mais ce n'était pas la bonne classe pour porter cette responsabilité. J'ai corrigé ça directement dans `Ticket` : le constructeur regarde maintenant ce que répond `reserverPlace()`, et si la réservation échoue, le ticket naît avec le statut "Refusé" au lieu de mentir sur son propre état.

### NIKIEMA P. Hanifah — la classe `Voyageur` et le schéma UML

Ma partie est plus courte niveau code, mais elle a son importance : la classe `Voyageur`, avec son numéro, son nom et son email, tous en privé, initialisés une bonne fois pour toutes par un seul constructeur. Je me suis aussi occupée du diagramme de classes dans `docs/`, en essayant de bien montrer les liens entre `Voyageur`, `Trajet` et `Ticket`, ainsi que l'héritage entre `Trajet`, `TrajetBus` et `TrajetVol`.

Le choix que j'ai dû défendre en groupe, c'est l'absence de setters pour le nom et l'email. Une fois qu'un voyageur est créé, rien ne change plus dans cette première version du programme, donc ajouter des setters n'aurait servi qu'à ouvrir une porte inutile pour modifier les données n'importe comment depuis `Main`, ce qu'on cherchait justement à éviter avec l'encapsulation.

Ma difficulté a surtout été de ne pas trop en faire sur le diagramme UML. On nous a bien précisé qu'un schéma simple suffisait, sans multiplicités détaillées, et j'avais tendance à vouloir tout représenter dans les moindres détails. Il a fallu que je me limite volontairement aux classes, aux attributs principaux et aux liens essentiels.

### NADEMBEGA Ingrid — la classe `Main`

Je me suis occupée de faire vivre tout ce que les autres ont codé. Dans `Main`, je crée deux voyageurs et deux trajets, un bus et un vol, je réserve une place sur chacun, puis je teste les deux cas d'annulation : celui qui doit passer (le bus, largement à l'avance) et celui qui doit être refusé (le vol, trop proche du départ). J'ai ajouté en plus le test de validation automatique du ticket et la vérification anti-double-réservation, comme demandé dans les questions de revue du sujet.

Le choix que j'ai fait, c'est de construire `Main` comme un vrai petit scénario plutôt qu'une suite de tests décousus. J'ai découpé l'affichage avec des titres du genre "=== Annulation refusée ===", pour que n'importe qui, même sans connaître le code, puisse suivre ce qui se passe rien qu'en lisant la console.

La difficulté que j'ai rencontrée, c'est de trouver des valeurs crédibles pour que le scénario tienne debout : il fallait un trajet avec assez de marge pour que l'annulation soit acceptée, et un autre juste trop proche du départ pour qu'elle soit refusée, sans tomber non plus sur des chiffres absurdes. J'ai dû ajuster plusieurs fois les heures avant départ avant que le scénario ait vraiment du sens.

J'ai repéré en testant que mon `Main` initial cachait en fait un bug : je vérifiais `estComplet()` avant de créer un ticket, mais si cette condition était fausse, la variable `ticket` restait à `null` et le programme plantait au premier `afficher()`. Une fois que Franck a corrigé `Ticket` pour qu'il gère lui-même ce cas, j'ai pu simplifier `Main` et j'en ai profité pour ajouter un dernier scénario avec un trajet créé directement à zéro place disponible, histoire de montrer clairement qu'un ticket peut naître "Refusé" sans jamais faire planter le programme.
