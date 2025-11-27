## 🔐 Keycloak Authentication Flow (UML Sequence Diagram)

Below is a Mermaid UML sequence diagram explaining how Keycloak integrates with the FeedApp backend.

```mermaid
sequenceDiagram
    autonumber
    participant U as User (Browser)
    participant FE as Frontend (SPA)
    participant KC as Keycloak Server
    participant BE as FeedApp Backend (Spring Boot)
    participant DB as Postgres Database

    Note over FE: 1. User clicks "Login"
    U->>FE: Opens the application
    FE->>KC: Redirect to Keycloak login page

    Note over KC: 2. User authenticates
    U->>KC: Enter username + password
    KC-->>FE: Returns Authorization Code (redirect back)

    Note over FE: 3. FE exchanges code for token
    FE->>KC: Token request (Client ID + Secret)
    KC-->>FE: Access Token (JWT)

    Note over FE: 4. FE calls the backend with JWT
    FE->>BE: GET /api/me (Authorization: Bearer <JWT>)

    Note over BE: 5. Spring Security validates JWT
    BE->>KC: (Optional) Download JWKS signing keys
    KC-->>BE: JWKS public keys

    BE->>BE: JwtAuthenticationConverter extracts roles & claims

    Note over BE: 6. Business logic execution
    BE->>DB: getOrCreateUser(keycloakId, email, username)
    DB-->>BE: User entity

    BE-->>FE: Returns user JSON response
```

### ✔ How GitHub renders this diagram

GitHub supports Mermaid diagrams out of the box.  
To render it:

1. Create or edit any Markdown file (e.g., `README.md`)  
2. Paste the entire code block above  
3. Push it to GitHub  
4. Git

