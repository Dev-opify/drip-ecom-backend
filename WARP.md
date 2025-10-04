# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

Project overview
- Backend: Java 17, Spring Boot 3 (Maven)
- Location: backend lives under dripyard-backend/
- Entrypoint: com.aditi.dripyard.DripyardBackendApplication
- HTTP: defaults to PORT=8080 (overridable via env)
- Docs: OpenAPI via springdoc at /swagger-ui/ and /v3/api-docs
- Health: Actuator at /actuator/health

Common commands
- Requirements: Java 17 and Maven (or use the Maven Wrapper in dripyard-backend/). All commands below use a repo-relative -f dripyard-backend/pom.xml to avoid changing directories.

Build
```bash path=null start=null
mvn -f dripyard-backend/pom.xml clean package
```

Run the app (dev)
```bash path=null start=null
mvn -f dripyard-backend/pom.xml spring-boot:run
```

Run the packaged JAR
```bash path=null start=null
java -jar dripyard-backend/target/dripyard-backend-0.0.1-SNAPSHOT.jar
```

Run tests (all)
```bash path=null start=null
mvn -f dripyard-backend/pom.xml test
```

Run a single test class
```bash path=null start=null
mvn -f dripyard-backend/pom.xml -Dtest=DripyardBackendApplicationTests test
```

Run a single test method
```bash path=null start=null
mvn -f dripyard-backend/pom.xml -Dtest=ClassName#methodName test
```

Skip tests during build
```bash path=null start=null
mvn -f dripyard-backend/pom.xml clean package -DskipTests
```

Clean build artifacts
```bash path=null start=null
mvn -f dripyard-backend/pom.xml clean
```

Docker
- A Dockerfile exists at repository root that builds using the Maven Wrapper inside the container and exposes the configured PORT.

Build image
```bash path=null start=null
docker build -t dripyard-backend .
```

Run container (example; provide required env vars as needed)
```bash path=null start=null
docker run --rm -p 8080:8080 \
  -e PORT=8080 \
  -e SPRING_DATASOURCE_URL={{SPRING_DATASOURCE_URL}} \
  -e SPRING_DATASOURCE_USERNAME={{SPRING_DATASOURCE_USERNAME}} \
  -e SPRING_DATASOURCE_PASSWORD={{SPRING_DATASOURCE_PASSWORD}} \
  dripyard-backend
```

Lint/format
- No code linting/formatting plugins are configured in pom.xml (e.g., Checkstyle/Spotless/PMD are not present). Use your IDE/formatter conventions as appropriate.

Environment configuration
These environment variables are referenced by application.properties. Set only what you need for your workflow; values are not included here.
- Database: SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
- MailerSend: MAILERSEND_API_TOKEN, MAILERSEND_ADMIN_EMAIL, MAILERSEND_FROM_EMAIL, MAILERSEND_FROM_NAME
- Payments: RAZORPAY_API_KEY, RAZORPAY_API_SECRET, STRIPE_API_KEY
- Shipping: SHIPROCKET_API_KEY, SHIPROCKET_API_SECRET, SHIPROCKET_API_BASEURL
- Cloudflare R2: CLOUDFLARE_R2_ACCESSKEY, CLOUDFLARE_R2_SECRETKEY, CLOUDFLARE_R2_ENDPOINT, CLOUDFLARE_R2_REGION, CLOUDFLARE_R2_BUCKET
- Contact metadata: CONTACT_EMAIL, CONTACT_PHONE
- Server: PORT (defaults to 8080)

High-level architecture
- Spring Boot application (com.aditi.dripyard): standard layered design.
  - config/
    - AppConfig: SecurityFilterChain with stateless sessions, JWT validation filter, and CORS. Most /api/** endpoints require authentication; swagger and some image/test endpoints are public.
    - JwtProvider: token generation and parsing with jjwt; roles embedded in authorities claim. JwtTokenValidator filter validates inbound JWTs.
    - WebConfig: MVC CORS mappings for allowed origins/methods.
    - R2Config (present) indicates integration with object storage (Cloudflare R2) via AWS SDK v2.
  - controller/: REST controllers for auth, users, products, carts, orders, payments, reviews, wishlist, shipping (Shiprocket), deals, coupons, images, email, helpdesk, admin, etc.
  - service/: domain services plus service/impl implementations (e.g., OrderServiceImplementation). Responsibilities cover business logic for the above resources as well as integrations (payments, email, storage, shipping). DataInitializationComponent can seed/init data.
  - repository/: Spring Data JPA repositories for each aggregate (ProductRepository, OrderRepository, etc.).
  - model/: JPA entities (User, Product, Order, Cart, Review, PaymentDetails, etc.) and supporting enums (OrderStatus, PaymentStatus, USER_ROLE, etc.).
  - dto/, request/, response/: transport-layer representations for inbound/outbound payloads and charts/summaries; mapper/ contains mapping helpers (e.g., OrderMapper, ProductMapper).
  - exception/: custom exceptions and a global exception handler (GlobleException) for API error shaping.
  - utils/: cross-cutting helpers (OtpUtils, EmailTemplate).

- Persistence: MySQL via spring-boot-starter-data-jpa with Hibernate (ddl-auto=update). Datasource configured by env vars.
- Security: spring-boot-starter-security with JWT-based auth; BCrypt for password hashing.
- Integrations:
  - Payments: Razorpay and Stripe SDKs
  - Email: MailerSend SDK and spring-boot-starter-mail
  - Storage: AWS SDK S3 client for Cloudflare R2
  - Shipping: Shiprocket APIs via Unirest/RestTemplate
- Operational:
  - Actuator health endpoint exposed; Dockerfile includes a HEALTHCHECK probing it.
  - API docs via springdoc-openapi-starter-webmvc-ui.

Notable paths
- Backend module: dripyard-backend/
- Main class: dripyard-backend/src/main/java/com/aditi/dripyard/DripyardBackendApplication.java
- App config (security/CORS): dripyard-backend/src/main/java/com/aditi/dripyard/config/AppConfig.java
- JWT provider: dripyard-backend/src/main/java/com/aditi/dripyard/config/JwtProvider.java
- Application properties: dripyard-backend/src/main/resources/application.properties
- Example test: dripyard-backend/src/test/java/com/aditi/dripyard/DripyardBackendApplicationTests.java
