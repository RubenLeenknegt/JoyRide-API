# Fantastic Lamp

## Project overzicht

Fantastic Lamp is een fullstack applicatie bestaande uit een Kotlin/Java backend en een MySQL database, gecontaineriseerd met Docker. Het project is opgezet met een CI/CD pipeline die automatisch bouwt, test, en deployt naar een VPS via Docker Hub. Het doel is een stabiele ontwikkel- en testworkflow, zodat features veilig en gecontroleerd uitgerold kunnen worden naar acceptatie en productie.

## Lokaal testen

Om de backend lokaal te draaien:

1. Vanuit de root van het project:
```bash
./gradlew :backend:build
docker-compose -f docker-compose.local.yml up --build
```

De applicatie start op http://localhost:8081 en maakt verbinding met een lokale MySQL-database via Docker.

## Workflow

Het project wordt ontwikkeld in wekelijkse sprints, genaamd Staging.1-2Test.

- Elke week wordt een nieuwe staging-branch aangemaakt in Git en wijzigingen worden hiernaar gepusht.
- Develop accepteert alleen pull requests vanaf staging-branches.
- Main accepteert alleen pull requests vanaf develop.
- Dit wordt afgedwongen via enforce-branch-sources.yml en GitHub Rulesets.

## Deployment

Bij push naar develop of main wordt de GitHub Action getriggerd. In simpele taal gebeurt het volgende:

1. **Code ophalen**: nieuwste code uit de repository.
2. **Java instellen**: Java 17 wordt klaargezet in een VM om de app te bouwen.
3. **Tests draaien**: alle Kotlin/Java tests in backend/src/test/kotlin en backend/src/test/java worden uitgevoerd.
4. **Backend bouwen**: de fat jar van de backend wordt gemaakt via shadowJar.
5. **Docker image maken**:
    - Develop → acceptatie-image (acc)
    - Main → productie-image (prod)
6. **Docker Hub push**: de images worden naar Docker Hub gestuurd.
7. **Deploy naar VPS**: via SSH worden oude containers gestopt/verwijderd, nieuwste images gepulled en de containers opnieuw gestart met de juiste docker-compose.

Kortom: bij elke push wordt de code getest, gebouwd, verpakt in Docker, naar Docker Hub gestuurd en automatisch op de VPS uitgerold.

## Tips voor lokale ontwikkeling

### Database resetten / initialiseren
```bash
docker-compose -f docker-compose.local.yml down
docker volume rm local_db_data
docker-compose -f docker-compose.local.yml up --build
```

### Omgevingsvariabelen
Zorg voor een .env.local met bijvoorbeeld:
```
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=testdb
 #this is a normal user
MYSQL_USER=local_user
MYSQL_PASSWORD=local_pass
```

### Logs bekijken
```bash
docker-compose -f docker-compose.local.yml logs -f backend
```

### Fat jar rebuilden
```bash
./gradlew :backend:build
docker-compose -f docker-compose.local.yml up --build
```

## Poorten en toegang

- **Backend lokaal**: http://localhost:8081
- **PHPMyAdmin**: http://localhost:8083

## Best practices

- Werk op aparte feature-branches.
- Test lokaal vóór push naar develop.
- Commit duidelijke messages en update de README indien nodig.

## Logging & Resources

- **Docker Hub**: fantastic-lamp-backend
- **GitHub Actions**: CI/CD Pipeline
- **Acceptatie omgeving**: http://\<server-ip\>:8081
- **SCP upload**:
  ```bash
  scp <filename> root@<server-ip>:~/fantastic-lamp/
  ```