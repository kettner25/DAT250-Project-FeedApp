# DAT250-Project-FeedApp

## Group17 
- Lubos Kettner & Matej Volkmer & Matthias Worz 
- HVL DAT250 Project
- Latest version
- DockerHub Image: <>

## Running
1. Clone the repository
2. Navigate to the project root
3. Run:
    ```bash
    docker compose up
    ```
4. Open: <http://localhost:8080/#all-polls>

## Keycloak
- Running on port 8081
- Web ui at <http://localhost:8081/realms/master/account>
- Default admin login: 
  - user: `admin`
  - pass: `admin`

## RabbitMQ
- Running on port 5672
- Web ui at <http://localhost:15672>
- Default admin login:
    - user: `guest`
    - pass: `guest`

## Redis
To connect:
```bash
redis-cli -p 6379
```

## Develop frontend
Run from project root:
```bash
npm --prefix frontend run de
```

## Docs
- Swagger UI at <http://localhost:8080/docs>
