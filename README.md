# Langly Backend

API REST Spring Boot pour la gestion des écoles de langues.

## Stack technique

- **Framework :** Spring Boot 3.5.7
- **Langage :** Java 21
- **Base de données :** PostgreSQL
- **Migrations :** Flyway
- **Cache :** Redis
- **Sécurité :** Spring Security + JWT
- **Conteneurisation :** Docker / Docker Compose
- **CI/CD :** Jenkins

## Structure du projet

```
src/main/java/com/langly/
├── app/
│   ├── Authority/          # Gestion des rôles et permissions
│   ├── course/             # Gestion des cours et séances
│   ├── email/              # Service d'envoi d'emails
│   ├── exception/          # Gestion globale des exceptions
│   ├── finance/            # Facturation, abonnements et paiements
│   ├── notification/       # Système de notifications
│   ├── school/             # Gestion des écoles
│   ├── shared/             # Utilitaires et éléments partagés
│   ├── student/            # Gestion des étudiants (profil, inscriptions, présence)
│   ├── user/               # Gestion des utilisateurs (CRUD, statuts)
│   └── DataSeeder.java     # Données initiales de seed
│
└── security/
    ├── config/             # Configuration Spring Security
    ├── jwt/                # Génération et validation des tokens JWT
    └── service/            # Service d'authentification
```

## Principaux endpoints

| Ressource | URL de base | Rôles autorisés |
|-----------|-------------|-----------------|
| Auth | `/api/v1/auth` | Public (login) |
| Users | `/api/v1/users` | SUPER_ADMIN, SCHOOL_ADMIN |
| Schools | `/api/v1/schools` | SUPER_ADMIN |
| Courses | `/api/v1/courses` | SCHOOL_ADMIN, TEACHER |
| Sessions | `/api/v1/sessions` | SCHOOL_ADMIN, TEACHER |
| Enrollments | `/api/v1/enrollments` | SCHOOL_ADMIN, TEACHER |
| Students | `/api/v1/students` | SCHOOL_ADMIN, TEACHER, STUDENT |
| Billings | `/api/v1/billings` | SCHOOL_ADMIN |
| Subscriptions | `/api/v1/subscriptions` | SUPER_ADMIN |

## Base de données

Les migrations Flyway se trouvent dans `src/main/resources/db/migration/`. Tables principales :

- `users`, `roles`, `permissions` — Authentification et autorisation
- `schools`, `subscriptions` — Écoles et abonnements
- `students`, `course`, `enrollment` — Parcours étudiant
- `sessions`, `attendance` — Séances et présence
- `billings`, `certifications`, `course_materials` — Facturation et ressources

## Lancement

```bash
# Avec Docker Compose
docker-compose up -d

# Avec Maven
./mvnw spring-boot:run
```
