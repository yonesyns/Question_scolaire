# Gestetud

API REST de gestion pédagogique en Spring Boot 3 / Java 17 et MySQL 8. Les données sont persistées dans MySQL ; les tables sont créées/mises à jour automatiquement au démarrage.

## Démarrer avec Docker

```bash
docker compose up --build
```

L'API est disponible sur `http://localhost:8080`. Au premier démarrage sur une base vide, l'application ajoute automatiquement 3 étudiants, 3 modules, 4 inscriptions, 9 évaluations, 12 notes et des séances/présences de démonstration. Le chargement est ignoré dès qu'un étudiant existe déjà en base. Pour démarrer l'API depuis l'IDE, lancer MySQL avec `docker compose up mysql -d`, puis `mvn spring-boot:run`. Les paramètres de connexion peuvent être fournis via `DB_URL`, `DB_USERNAME` et `DB_PASSWORD` (valeurs locales par défaut : `root` / `root`).

## Endpoints

- `GET/POST /students`, `GET/PUT/DELETE /students/{id}` : CRUD étudiant (`name`, `email`, `program`).
- `GET/POST /courses`, `GET/PUT/DELETE /courses/{id}` : CRUD module (`code`, `title`, `capacity`).
- `POST /registrations` avec `studentId` et `courseId` : inscrit si les deux existent, si l'inscription n'existe pas déjà, si la capacité est disponible et si les prérequis sont validés.
- `GET /students/{id}/registrations` : inscriptions d'un étudiant.
- `GET /courses/{courseId}/prerequisites` : consulter les prérequis d'un module.
- `POST /courses/{courseId}/prerequisites` avec `prerequisiteCourseId`, `equivalentCourseId` facultatif et `minimumGrade` facultatif (10/20 par défaut) : ajouter une règle.
- `DELETE /courses/{courseId}/prerequisites/{prerequisiteCourseId}` : retirer un prérequis.
- `GET/POST /exams` : consulter/créer une évaluation (`courseId`, `date` au format `YYYY-MM-DD`, `title`, `evaluationTypeCode`).
- `POST /grades` avec `examId`, `studentId`, `value` (0 à 20) : saisit une note si l'étudiant est inscrit au module de l'examen.
- `GET /students/{id}/grades` : notes de l'étudiant.
- `GET/POST /courses/{courseId}/sessions` : consulter/créer les séances du module (`date`, `title`).
- `GET /sessions/{sessionId}/attendance` : consulter les présences d'une séance.
- `PUT /sessions/{sessionId}/attendance/{studentId}` avec `status` égal à `PRESENT`, `ABSENT` ou `EXCUSED` : saisir/modifier une présence.
- `GET /students/{studentId}/courses/{courseId}/exam-eligibility` : consulter le taux d'absence et l'éligibilité à l'examen final.
- `GET/POST/PUT/DELETE /evaluation-types` : consulter et gérer les types d'évaluation (`code`, `label`).
- `GET/PUT /courses/{courseId}/evaluation-rules` : consulter/remplacer en une fois les pondérations d'un module. La somme des poids doit faire 100.
- `GET /students/{studentId}/courses/{courseId}/result` : calculer le résultat pondéré à partir des notes du module.

Un prérequis est validé si l'historique contient une note au moins égale au seuil dans le module requis ou dans son module équivalent. Le seuil de prérequis s'applique à la moyenne finale pondérée du module préalable, et seules les notes d'examens déjà passés comptent. Une absence d'historique et un historique non validé produisent des messages distincts (409). Les relations qui créeraient une boucle sont refusées. Les erreurs de ressource inconnue renvoient 404 ; les inscriptions impossibles renvoient 409. Pour consulter les données chargées : `GET /students`, `GET /courses`, `GET /exams`, `GET /students/1/registrations` et `GET /students/1/grades`. Les exemples de requêtes sont dans `postman/Gestetud.postman_collection.json`.


## Calcul des résultats

La formule par défaut est Contrôle continu 30 %, Examen 50 %, Projet 20 %. La moyenne de chaque catégorie est calculée à partir de toutes ses notes, puis les catégories sont pondérées selon la règle du module. Le résultat final (`finalAverage`) est renseigné lorsque chaque catégorie pondérée a au moins une note et que l’étudiant est éligible à l’examen final. Avant cela, `provisionalAverage` représente la moyenne des catégories déjà évaluées, et `coveredWeight` indique la part de la formule couverte.

Exemple de configuration :

```json
[
  {"evaluationTypeCode":"CONTINUOUS","weight":30},
  {"evaluationTypeCode":"EXAM","weight":50},
  {"evaluationTypeCode":"PROJECT","weight":20}
]
```


## Présences et examen final

Le pourcentage d’absence est calculé sur les séances du module déjà tenues ; seules les présences marquées `ABSENT` sont comptées (`EXCUSED` est exclu). À exactement 20 %, l’étudiant reste éligible ; au-delà, la saisie d’une note pour un examen déclaré final (`finalExam: true`) est refusée. Le calcul du résultat indique `publicationStatus` (`PUBLISHED`, `INCOMPLETE` ou `INELIGIBLE`) et n’expose pas de `finalAverage` si l’étudiant dépasse le seuil.
