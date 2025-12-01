# Bajaj Finserv Health — Qualifier 1 (Java)

Small Spring Boot application that performs the mandatory startup flow for the qualifier:

- On startup it sends a POST to the generateWebhook endpoint with registration details.
- It receives a webhook URL and an accessToken in the response
- Based on the last two digits of the regNo (odd/even) the app chooses a final SQL query
- It stores the final SQL in `final_query.sql` and sends the final query back to the webhook with JWT in the `Authorization` header.

How to build and run
--------------------

Requirements: JDK 17 and Maven

Build:

```bash
cd ~/bajaj-finserv-qualifier
mvn -DskipTests package
```

Run the jar:

```bash
java -jar target/bajaj-finserv-qualifier-0.1.0.jar
```

Notes
-----
- There is no HTTP controller — the flow is triggered automatically during startup (ApplicationRunner).
- The current repository includes fallback behavior: if the remote generateWebhook cannot be reached the app will continue with a mocked response and will attempt to post the final query to the `testWebhookPath`.
- The final SQL for Question 1 (odd regNo) has been embedded into `SQLSolver` and will be submitted if your `regNo` ends in an odd number. Replace or update it if needed.

Submission checklist / CI
------------------------

To complete the qualifier submission you must provide a public GitHub repository with:

1. All project source code (this repo)
2. The final JAR (target/bajaj-finserv-qualifier-0.1.0.jar) and a raw downloadable link to it

There is a GitHub Actions workflow included at `.github/workflows/maven-ci.yml` which will automatically build and upload the JAR as an artifact named `bajaj-finserv-qualifier-jar` when you push to `main` or `master`. After the workflow finishes you can download the artifact and use that raw link in the submission form: https://forms.office.com/r/WFzAwgbNQb

If you want I can also add the final SQL as a separate `final_query.sql` in the repository to make the final answer easy to inspect; tell me if you'd like that added.
# Bajaj Finserv Health — Qualifier 1 (Java)

Small Spring Boot application that performs the mandatory startup flow for the qualifier:

- On startup it sends a POST to the generateWebhook endpoint with registration details.
- It receives a webhook URL and an accessToken in the response
- Based on the last two digits of the regNo (odd/even) the app chooses a final SQL query
- It stores the final SQL in `final_query.sql` and sends the final query back to the webhook with JWT in the `Authorization` header.

How to build and run
--------------------

Requirements: JDK 17 and Maven

Build:

```bash
cd ~/bajaj-finserv-qualifier
mvn -DskipTests package
```

Run the jar:

```bash
java -jar target/bajaj-finserv-qualifier-0.1.0.jar
```

Notes
-----
- There is no HTTP controller — the flow is triggered automatically during startup (ApplicationRunner).
- The current repository includes fallback behavior: if the remote generateWebhook cannot be reached the app will continue with a mocked response and will attempt to post the final query to the `testWebhookPath`.
- Replace the placeholder SQL queries in `SQLSolver` with the actual problem solutions if you have them.
Configuration & submission checklist
----------------------------------

- Edit `src/main/resources/application.yml` to set your name, regNo and email if you want custom values.
- The flow is automatic on startup — there are no controllers or manual triggers.
- After building, the jar will be located at `target/bajaj-finserv-qualifier-0.1.0.jar` — add it to your public GitHub repo and upload to the submission form.

Packaging note: if you don't have Maven installed locally you can either install it or use a container image with Maven to build the artifact. Example (Docker):

```bash
docker run --rm -v "$PWD":/workspace -w /workspace maven:3.8.8-openjdk-17-slim mvn -DskipTests package
```

