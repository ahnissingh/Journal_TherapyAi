
**Journal TherapyAI** is a Spring Boot 3 (Java 21) based mental health journaling platform designed to support emotional wellness through guided self-reflection and AI-driven insights. Writing about personal thoughts and feelings can significantly improve mental health by helping manage anxiety, stress, and depression. This project’s vision is to create a safe, user-friendly space for individuals to journal regularly, while also empowering therapists with tools to better understand and assist their clients.

The application provides secure, role-based accounts for **Users**, **Therapists**, and **Administrators**. Users can write private journal entries (with gamified streaks and reminders), and engage with an AI-powered chatbot for therapy guidance. Therapists can access consented clients’ journals, review automated analysis, and monitor progress. Under the hood, the backend follows modern best practices: it is built on **Spring Boot 3** (Java 21) using a modular *pack-by-feature* architecture that is ready for future microservices migration. Data is persisted in **MongoDB Atlas**, and each journal entry is encoded into vector embeddings stored in **Milvus (Zilliz Cloud)** for semantic search and AI-driven recommendations. All network communication uses HTTPS (HTTP/2), and security is enforced with Spring Security and JWT-based authentication.

## Key Features

* **Role-Based Access:** Secure authentication with Spring Security and JWT, assigning roles (USER, THERAPIST, ADMIN) to control permissions and API access.
* **User Journaling:** Users can write, edit, and track journal entries privately. The app includes gamification (streak tracking) and sends notification reminders for daily journaling.
* **Therapist Dashboard:** Therapists have a dedicated interface to view and annotate client journals (with permission), track client progress over time, and manage therapy sessions.
* **AI-Powered Chatbot:** An integrated GPT-4 based chatbot guides users through therapeutic exercises. Using Spring AI’s tool-calling support, the bot analyzes user preferences and journal entries to provide personalized coping strategies and emotional support.
* **Auto-Generated Reports:** The platform automatically generates periodic therapy reports by analyzing journal data. These reports (e.g. weekly/monthly summaries) are created using generative AI and sent via email. Sending and scheduling emails is handled by **SendGrid** and Spring’s scheduling support (leveraging Java’s virtual threads for scalability).
* **Notifications & Reminders:** Users receive email and in-app notifications to encourage consistent journaling. Reminders (via SendGrid) and system alerts are scheduled using Spring’s `@Scheduled` tasks and run on virtual threads for efficiency.
* **Caching & Performance:** Frequently accessed data (e.g. user profiles, lookup tables) is cached using **Caffeine** to reduce latency. The app is production-ready with SSL/TLS configured, and supports both development and production profiles for flexible deployments.
* **Admin Operations:** Administrators can manage user accounts, roles, and global settings through a secure admin interface. They can also monitor system health and logs, and perform maintenance tasks (backup, cleanup, etc.).

## Technologies Used

* **Java 21 & Spring Boot 3:** Leverages the latest Java LTS and Spring ecosystem (Spring Framework 6 / Spring Boot 3 support Java 21) for a robust, performant backend.
* **Spring AI (OpenAI GPT-4):** Uses Spring AI to integrate OpenAI’s ChatGPT models. The `spring.ai.openai.api-key` property (from environment variables) enables GPT-4 access. Tool-calling features allow the AI to interact with custom services.
* **MongoDB Atlas:** Cloud-hosted NoSQL database for storing user data, journal entries, and metadata.
* **Milvus (Zilliz Cloud):** A high-performance vector database for storing embeddings of journal entries. Milvus supports real-time similarity search over millions of vectors, enabling advanced insights and recommendations.
* **Spring Security & JWT:** Secures all endpoints with HTTPS and JWT tokens. Uses role-based access control (RBAC) to differentiate Users, Therapists, and Admins.
* **Caffeine Cache:** In-memory caching library to improve response times and reduce database load.
* **Testcontainers:** Docker-based testing support so integration tests can spin up MongoDB and Milvus containers on the fly. Ensures tests run with realistic backends.
* **SendGrid:** Cloud email service for reliable delivery of notifications and reports.
* **Build & Tools:** Maven or Gradle build; JUnit 5 and Mockito for testing; Docker/Kubernetes for containerization. Also uses Java virtual threads (Project Loom) to simplify concurrency and scheduling.

## System Architecture Summary

The backend follows a **feature-oriented modular design**. Each domain (user management, journaling, analysis, chatbot, notifications) resides in its own package/module. REST controllers expose JSON APIs, which delegate to service classes for business logic. Data access is performed by repository classes (Spring Data for MongoDB, and Milvus client for vector operations).

Client applications (e.g. a web or mobile frontend) communicate with the backend over HTTPS. Incoming requests pass through Spring Security filters (JWT authentication) before reaching controllers. When a user submits a journal entry, the text is saved in MongoDB Atlas, and simultaneously processed to generate an embedding that is stored in Milvus. This dual storage enables both traditional data queries and semantic search. The AI chatbot component pulls relevant journal data and context to formulate prompts for GPT-4; thanks to Spring AI tool calling, the bot can also invoke custom “tools” (methods) for tasks like scheduling appointments or analyzing entry sentiment.

Scheduled background tasks (annotated with `@Scheduled`) perform routine operations: for example, analyzing user journals to generate weekly therapy reports, which are compiled using the AI and then emailed via SendGrid. The application supports two Spring profiles: **dev** (for local testing) and **prod** (for deployed environments). Environment-specific configurations (MongoDB URI, SendGrid API key, etc.) are externalized and injected at runtime, allowing seamless switching between development and production setups. All communication with external services (OpenAI, Milvus, SMTP) uses secure channels, and HTTP/2 is enabled for efficiency.

## How to Run

1. **Prerequisites:** Install Java 21 (JDK 21+), Maven or Gradle, and Docker (for running integration tests).
2. **Clone the Repository:**

   ```bash
   git clone https://github.com/ahnisaneja/Journal_TherapyAi.git
   cd Journal_TherapyAi
   ```
3. **Configure Environment:** Create a `application.yml` or set environment variables for sensitive settings. At minimum, set:

   * `SPRING_DATA_MONGODB_URI` (connection string to MongoDB Atlas)
   * `MILVUS_URL` (Milvus vector DB endpoint)
   * `SPRING_AI_OPENAI_API_KEY` (OpenAI API key for GPT-4)
   * `SENDGRID_API_KEY` (SendGrid API key)
   * `JWT_SECRET` (secret key for signing JWT tokens)
   * `TLS_KEYSTORE_PATH` and `TLS_KEYSTORE_PASSWORD` (for SSL certificate in prod)
     Spring Boot will inject these into `application-prod.yml` or `application.yml` using the `${...}` syntax.
4. **Run in Development Mode:** By default, the `dev` profile uses unsecured HTTP and may connect to local services (if configured). Start the app with:

   ```bash
   mvn spring-boot:run
   ```

   The service will listen on port 8080 (HTTP). Use tools like curl or Postman to test endpoints (e.g. `/api/auth/login`).
5. **Run in Production Mode:** Ensure all production environment variables are set (especially SSL keystore). Build the executable jar:

   ```bash
   mvn clean package
   ```

   Then run with the `prod` profile:

   ```bash
   java -jar -Dspring.profiles.active=prod target/Journal_TherapyAi.jar
   ```

   The application will start on port 8443 (HTTPS) by default. Access it via `https://<server>:8443`.
6. **Running Tests:** The project includes unit and integration tests. To run them:

   ```bash
   mvn test
   ```

   Integration tests will automatically start MongoDB and Milvus containers via Testcontainers, so no manual database setup is required for testing.

## Testing Setup

The codebase has comprehensive test coverage. Unit tests (JUnit 5 + Mockito) are used for individual components. Integration tests leverage **Testcontainers** to provide disposable Docker environments for MongoDB and Milvus. This ensures tests interact with real services (using the same client libraries as in production). Simply running `mvn test` on a machine with Docker will spin up containers automatically, run the tests, and then tear them down. This makes it easy to verify behavior end-to-end without manual service setup.

## Security and Environment Configuration

Security is enforced throughout the application. All API routes require HTTPS and JWT authentication. Spring Security is configured with RBAC so that only authorized roles can access sensitive endpoints (e.g. only `THERAPIST` can access client data for assigned users). Passwords and secrets are stored hashed or encrypted (never in plaintext). We **never commit API keys or credentials** to the repository. Instead, Spring Boot’s support for externalized configuration is used: values in `application.yml` are injected from environment variables or command-line arguments. For example, the MongoDB URI or OpenAI key might be referenced as `${MONGODB_URI}` in the config. Production deployments must provide these env vars (e.g., via a secrets manager or CI/CD pipeline). HTTPS/TLS is configured using a keystore; HTTP/2 is enabled for performance.

By following these practices, the application remains secure by default. Regular dependencies are up-to-date, and sensitive endpoints (authentication, data access) include rate-limiting and validation as needed.

## Contribution Guidelines

We welcome contributions! Please read our guidelines before contributing:

* **Fork the repository** and create a feature branch (`git checkout -b feature/my-feature`).
* **Implement new features or bug fixes** with clear, well-documented code. Include Javadoc for public methods.
* **Write tests** for any new functionality (unit tests for components, integration tests for new services).
* **Code style:** Follow standard Java conventions (use `mvn spotless:apply` or your IDE’s formatter).
* Submit a **Pull Request (PR)** to the `main` branch with a detailed description of changes. Each PR will be reviewed for code quality, style, and completeness of tests.
* Use **GitHub Issues** to report bugs or request features. Provide logs or screenshots if possible.
* Abide by the **Contributor Covenant Code of Conduct** (see `CODE_OF_CONDUCT.md`).
**Enjoy journaling and contributing towards better mental health!**
