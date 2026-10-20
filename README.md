# Grand Cinéma

Application web de réservation de places pour le cinéma Grand Cinéma (cas d'école).

Ce dépôt contient l'API. Le front est dans le dépôt `grand-cinema-front`.

## Technologies

- Java 17
- Spring Boot
- MariaDB

## Lancement

Prérequis : Java 17, Maven et Docker Desktop

```
mvn spring-boot:run
```

Spring Boot démarre MariaDB avec `compose.yaml`, qui crée les tables (`model.sql`) et les données de test (`data.sql`). L'API répond sur `http://localhost:8080`.

## Tests

```
mvn test
```

## Swagger

Avec l'application lancée : `http://localhost:8080/swagger-ui.html`.

## Organisation Git

- `main` : version stable. On ne développe pas directement dessus.
- `develop` : branche d'intégration, à partir de laquelle sont créées les branches `feature/...`.

## Convention de nommage des branches

Format : `type/nom-court`, en français, en minuscules, sans accents, mots séparés par des tirets.

- `feature/` : une fonctionnalité (User Story), par exemple `feature/selection-places`
- `hotfix/` : une correction urgente, par exemple `hotfix/erreur-reservation`
- `release/` : une version, par exemple `release/v1.0`
