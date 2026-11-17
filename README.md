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
- `POST /registrations` avec `studentId` et `courseId` : inscrit l'étudiant (`ACTIVE`) ou le place en `WAITING_LIST` si le module est complet, après vérification des prérequis.
- `GET /courses/{courseId}/waitlist` : consulter la liste d'attente dans l'ordre d'arrivée.
- `DELETE /registrations/{registrationId}` : annuler une inscription ; si elle était active, le premier étudiant en attente est promu automatiquement.
- `GET /students/{id}/registrations` : inscriptions d'un étudiant.
- `GET /courses/{courseId}/prerequisites` : consulter les prérequis d'un module.
- `POST /courses/{courseId}/prerequisites` avec `prerequisiteCourseId`, `equivalentCourseId` facultatif et `minimumGrade` facultatif (10/20 par défaut) : ajouter une règle.
- `DELETE /courses/{courseId}/prerequisites/{prerequisiteCourseId}` : retirer un prérequis.
- `GET/POST/PUT /exams` : consulter/créer/modifier une évaluation (`courseId`, `date` au format `YYYY-MM-DD`, `title`, `evaluationTypeCode`).
- `POST /grades` avec `examId`, `studentId`, `value` (0 à 20) : saisit une note si l'étudiant est inscrit au module de l'examen.
- `GET /students/{id}/grades` : notes de l'étudiant.
- `POST /courses/{courseId}/results/publish` : publie les résultats finaux disponibles des étudiants actifs et déclenche leurs notifications.
- `GET/POST/PUT /courses/{courseId}/sessions` : consulter/créer/modifier les séances du module (`date`, `title`).
- `GET /sessions/{sessionId}/attendance` : consulter les présences d'une séance.
- `PUT /sessions/{sessionId}/attendance/{studentId}` avec `status` égal à `PRESENT`, `ABSENT` ou `EXCUSED` : saisir/modifier une présence.
- `GET /students/{studentId}/courses/{courseId}/exam-eligibility` : consulter le taux d'absence et l'éligibilité à l'examen final.
- `GET/POST/PUT/DELETE /evaluation-types` : consulter et gérer les types d'évaluation (`code`, `label`).
- `GET/PUT /courses/{courseId}/evaluation-rules` : consulter/remplacer en une fois les pondérations d'un module. La somme des poids doit faire 100.
- `GET /students/{studentId}/courses/{courseId}/result` : calculer le résultat pondéré à partir des notes du module.
- `POST /courses/{courseId}/results/publish` : enregistrer et publier les résultats finaux disponibles, puis notifier les étudiants.

Un prérequis est validé si l'historique contient une note au moins égale au seuil dans le module requis ou dans son module équivalent. Le seuil de prérequis s'applique à la moyenne finale pondérée du module préalable, et seules les notes d'examens déjà passés comptent. Une absence d'historique et un historique non validé produisent des messages distincts (409). Les relations qui créeraient une boucle sont refusées. Les erreurs de ressource inconnue renvoient 404 ; les inscriptions impossibles renvoient 409. Pour consulter les données chargées : `GET /students`, `GET /courses`, `GET /exams`, `GET /students/1/registrations` et `GET /students/1/grades`. Les exemples de requêtes sont dans `postman/Gestetud.postman_collection.json`.


## Calcul des résultats

La formule par défaut est Contrôle continu 30 %, Examen 50 %, Projet 20 %. La moyenne de chaque catégorie est calculée à partir de toutes ses notes, puis les catégories sont pondérées selon la règle du module. Le résultat final (`finalAverage`) est renseigné lorsque chaque catégorie pondérée a au moins une note et que l’étudiant est éligible à l’examen final. `publicationStatus` vaut `READY` avant publication et `PUBLISHED` après publication. Avant cela, `provisionalAverage` représente la moyenne des catégories déjà évaluées, et `coveredWeight` indique la part de la formule couverte.

Exemple de configuration :

```json
[
  {"evaluationTypeCode":"CONTINUOUS","weight":30},
  {"evaluationTypeCode":"EXAM","weight":50},
  {"evaluationTypeCode":"PROJECT","weight":20}
]
```


## Présences et examen final

Le pourcentage d’absence est calculé sur les séances du module déjà tenues ; seules les présences marquées `ABSENT` sont comptées (`EXCUSED` est exclu). À exactement 20 %, l’étudiant reste éligible ; au-delà, la saisie d’une note pour un examen déclaré final (`finalExam: true`) est refusée. Le calcul du résultat indique `publicationStatus` (`READY`, `PUBLISHED`, `INCOMPLETE` ou `INELIGIBLE`) et n’expose pas de `finalAverage` si l’étudiant dépasse le seuil.


## Inscriptions et liste d’attente

Les inscriptions, annulations et promotions sont exécutées dans une transaction. Un verrou pessimiste sur la ligne du module sérialise les inscriptions concurrentes : la capacité active n’est pas dépassée. Les nouvelles demandes lorsque le module est complet reçoivent le statut `WAITING_LIST`; elles sont ordonnées par date de création, puis par identifiant. Annuler une demande en attente la retire de la file sans promotion. Annuler une inscription active libère la place et promeut la première demande en attente. Une inscription annulée peut être redemandée ; si le module est plein, elle rejoint la fin de la file.


## Notifications multicanales

Les notifications sont centralisées, envoyées après validation de la transaction, et enregistrées avec leur état `SENT` ou `FAILED` et le détail de l’erreur. Les déclencheurs sont l’acceptation/promotion d’une inscription, la création ou modification d’une séance, la modification d’un examen, et la publication des résultats.

- `GET/PUT /students/{studentId}/notification-preferences` : préférences email/SMS/WhatsApp et numéro de téléphone. L’email est activé par défaut ; SMS et WhatsApp sont désactivés par défaut.
- `GET /notification-templates` et `PUT /notification-templates/{eventType}/{channel}` : consulter/personnaliser les modèles (`REGISTRATION_ACCEPTED`, `SCHEDULE_CHANGED`, `GRADES_PUBLISHED`; `EMAIL`, `SMS`, `WHATSAPP`). Les champs entre doubles accolades sont remplacés par les données de l’événement.
- `GET /students/{studentId}/notifications` : historique des tentatives d’envoi et erreurs.

L’email utilise la configuration SMTP Spring (`SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`). SMS et WhatsApp utilisent l’API de messagerie Twilio : configurer `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_SMS_FROM` et `TWILIO_WHATSAPP_FROM`. Le numéro destinataire doit être au format international E.164 ; WhatsApp nécessite un expéditeur WhatsApp Twilio configuré. Les messages WhatsApp utilisent les adresses de canal `whatsapp:` prévues par Twilio. Sans fournisseur configuré, la tentative est conservée en `FAILED` avec une erreur explicite ; l’application continue de fonctionner.
