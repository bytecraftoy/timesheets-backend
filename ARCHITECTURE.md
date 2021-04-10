# Timesheets architectural overview

### Scala

### sbt

### Scalafmt

### Play

### Root scope

com.codeflow.timesheets

### Packages

1.  Web

    1.  Routes

    2.  Controllers

    3.  Data Transfer Objects (DTOs)

        Case Classes without methods

2.  Domain

    1.  Models

        Use case classes. Describe attributes and associations, but do
        not include methods.

        1.  Project

        2.  User

    2.  Services

        - Changes Data Transfer Objects to models and calls Data
          Access Objects.
        - The data access operations should be in the persistence
          layer (e.g. dao.add()).
        - Changes data from Data Access Objects to Data Transfer
          Objects

3.  Persistence

    1.  Data Access Objects (DAOs)

# Domain language

## English

| Concept           | Definition                                                                                                                                                              | Synonyms          |
| ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------- |
| Application       | The software program that tracks and maintains timesheets. Used by an organization.                                                                                     | App               |
| Application Owner | The individual or group with the responsibility to ensure that the program(s) making up the application accomplishes its objectives                                     |                   |
| Architecture      | Structural composition of the application and its supporting technologies                                                                                               |                   |
| Billing           | The process of preparing or sending invoices.                                                                                                                           |                   |
| Budget            | Project's planned costs in time and money.                                                                                                                              |                   |
| Client            | A person or organization using the services of a company. May buy several projects.                                                                                     | Customer<br>Buyer |
| Cost              | The time and money that has been used to execute a project.                                                                                                             |                   |
| Employee          | A person billing hours in a project but not having any administrative or managerial role.                                                                               |                   |
| Manager           | A person responsible for controlling or administering an organization or group of staff.                                                                                | Supervisor        |
| Project           | An individual or collaborative enterprise that is planned to achieve an aim. Takes time and one or more tasks to complete.                                              |                   |
| Report            | A human-readable and printable summary of a user-specified context.                                                                                                     |                   |
| Task              | A piece of work to be done or undertaken. Takes time.                                                                                                                   |                   |
| Task description  | Description of what work was done or how time was spent during a task.                                                                                                  | Comment           |
| Time              | The difference in numbers in a clock between the beginning and the end of a work session. Can be represented as hours with decimal fractions or with hours and minutes. |                   |
| Timesheets        | Collective record of tasks and time spent working. The main purpose of this application.                                                                                |                   |
| User              | A person who uses the application.                                                                                                                                      | End user          |

## Finnish

| Käsite               | Määritelmä                                                                                                                                      | Synonyymejä              |
| -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------ |
| Sovellus             |                                                                                                                                                 |                          |
| Sovelluksen omistaja | Yksilö tai ryhmä, joiden vastuulla on, että sovelluksen muodostavat ohjelmat saavuttavat tavoitteensa.                                          |                          |
| Arkkitehtuuri        |                                                                                                                                                 |                          |
| Laskutus             | Kuittien tai laskujen valmistelu tai lähettämisprosessi.                                                                                        |                          |
| Budjetti             | Projektin suunnitellut tunti- ja rahalliset kustannukset.                                                                                       | Talousarvio              |
| Asiakas              | Henkilö tai organisaatio, joka käyttää yrityksen palveluja. Voi ostaa useita projekteja.                                                        | Toimeksiantaja<br>Ostaja |
| Kustannus            | Projektin suorittamiseen jo käytetyt tunnit ja raha.                                                                                            | Toteutunut kustannus     |
| Työntekijä           | Henkilö, joka tekee laskutettavia tunteja projekteissa, mutta ei ole johtavassa tai hallinnollisessa roolissa.                                  | Alainen                  |
| Manageri             | Henkilö, joka on vastuussa organisaation tai työntekijöiden hallinnoinnista.                                                                    | Johtaja<br>Esihenkilö    |
| Projekti             | Yksilön tai yhteinen hanke, joka on suunniteltu päämäärän saavuttamiseksi. Vie aikaa ja vaatii yhden tai useamman tehtävän.                     | Hanke<br>Urakka          |
| Raportti             | Ihmisselkoinen ja tulostettava yhteenveto halutusta asiasta                                                                                     |                          |
| Tehtävä              | Suoritettava työ, joka vie aikaa.                                                                                                               |                          |
| Tehtävän kuvaus      | Kuvaus tehdystä työstä tai kuinka aikaa käytettiin tehtävän suorittamiseen.                                                                     |                          |
| Aika                 | Erotus kellon numeroissa työsession alun ja ja lopen välillä. Voidaan esittää tunnin kymmenkantaisina desimaaleina tai tunteina ja minuutteina. |                          |
| Työaikakortti        | Tehtävien ja niiden viemän ajan luettelo                                                                                                        | Kellokortti              |
| Käyttäjä             | Henkilö joka käyttää sovellusta                                                                                                                 | Loppukäyttäjä            |

# Tool installation

### sbt, openjdk

1.  MacOS

    ```shell
    brew install openjdk

    brew install sbt
    ```

### PostgreSQL

1.  MacOS

    ```shell
    brew install postgresql
    pg_ctl -D /usr/local/var/postgres start
    psql
    ```

    ```sql
    CREATE ROLE "timesheet" SUPERUSER LOGIN ENCRYPTED PASSWORD 'tähän salasana';
    ```

2.  application.conf:

    Laita Ideassa sbt:n parametreilla ympäristömuuttujiksi ao. kuvan
    arvot, niin ei tarvitse muokata application.confia.

    HUOM! Koskee vain Idean sbt shelliä. Muihin terminaaleihin pitää
    määrittää esim. sbt:n käynnistysparametrina.

        db.timesheet.driver=org.postgresql.Driver

        db.timesheet.url="jdbc:postgresql://localhost:5432/postgres"

        db.timesheet.username=timesheet

        db.timesheet.password=tähän salasana

# Database

## PostgreSQL

## H2

## Play Evolutions

- There is a conflict when adding new information from frontend to the
  database and then rolling evolutions back

## Schema

# Swagger

<https://codeflow-timesheets-staging.herokuapp.com/docs/swagger-ui/index.html?url=/swagger.json>

### Known security vulnerability: `+ nocsrf`

In order to get `POST` API calls working in Swagger UI, we had to
disable Play's CSRF by adding `+ nocsrf` annotation above the affected
routes in the [file:./conf/routes](./conf/routes) file. This creates a
security vulnerability.

# Authorizations

| Routes                              | Affected methods                         | Use case                                           | Manager | Employee | Specific user |
| ----------------------------------- | ---------------------------------------- | -------------------------------------------------- | ------- | -------- | ------------- |
| GET _projects:id_/hours             | TimeInputController.byProject            | User can view own timeinputs.                      |         |          | x             |
| POST /hours                         | TimeInputController.add                  | User can add own timeinputs.                       |         |          | x             |
| PUT /hours                          | TimeInputController.update               | User can edit own timeinputs.                      |         |          | x             |
| GET /projects                       | ProjectController.listProjects           | User can view own projects.                        |         |          | x             |
| GET /projects                       | ProjectController.listProjects           | Manager can view all projects.                     | x       |          |               |
| POST /projects                      | ProjectController.addProject             | Manager can add a project.                         | x       |          |               |
| GET /clients                        | ClientController.listClients             | Manager can add a project.                         | x       |          |               |
| GET /managers                       | ManagerController.listManagers           | Manager can add a project.                         | x       |          |               |
| GET /clients                        | ClientController.listClients             | Manager can generate a billing report.             | x       |          |               |
| GET _clients:id_/projects           | ProjectController.listProjectsByClientId | Manager can generate a billing report.             | x       |          |               |
| GET /projects/employees             | ProjectController.getUsersByProject      | Manager can generate a billing report.             | x       |          |               |
| GET report/_client:id_              | ReportController.getClientReport         | Manager can generate a billing report.             | x       |          |               |
| GET /employees                      | EmployeeController.listEmployees         | Manager can generate a salary report for anyone.   | x       |          |               |
| GET employees/_employee:id_/clients | EmployeeController.listClientsOfEmployee | Manager can generate a salary report for anyone.   | x       |          |               |
| GET report/_employee:id_            | ReportController.getSalaryReport         | Manager can generate a salary report for anyone.   | x       |          |               |
| GET _employees:id_/clients          | EmployeeController.listClientsOfEmployee | Employee can generate a salary report for oneself. |         |          | x             |
| GET report/_employee:id_            | ReportController.getSalaryReport         | Employee can generate a salary report for oneself. |         |          | x             |
