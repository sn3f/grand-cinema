# MariaDB avec les tables et les données de test, chargées au premier démarrage
FROM mariadb:13.0
COPY model.sql /docker-entrypoint-initdb.d/1-model.sql
COPY data.sql /docker-entrypoint-initdb.d/2-data.sql
