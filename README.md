# Grand Cinéma

Application web de réservation de places pour le cinéma Grand Cinéma (cas d'école).

## Technologies

- Java 17
- Spring Boot
- MariaDB

## Organisation Git

- `main` : version stable. On ne développe pas directement dessus.
- `develop` : branche d'intégration, à partir de laquelle sont créées les branches `feature/...`.

## Convention de nommage des branches

Format : `type/nom-court`, en français, en minuscules, sans accents, mots séparés par des tirets.

- `feature/` : une fonctionnalité (User Story), par exemple `feature/selection-places`
- `hotfix/` : une correction urgente, par exemple `hotfix/erreur-reservation`
- `release/` : une version, par exemple `release/v1.0`
