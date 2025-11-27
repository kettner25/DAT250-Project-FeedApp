plugins {
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.6"
    id("java")
}

group = "no.hvl.group17"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // ==== Spring Boot Core ====
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ==== PostgreSQL ====
    runtimeOnly("org.postgresql:postgresql")

    // ==== Password Hashing ====
    implementation("org.springframework.security:spring-security-crypto")

    // ==== JWT (AUTH0 – kein OAuth2 notwendig!) ====
    implementation("com.auth0:java-jwt:4.4.0")

    // ==== Lombok ====
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // ==== Testing ====
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // JJWT (JSON Web Token) – vollständig für Parsing, Signing, Schlüssel)
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.data:spring-data-redis")
    implementation("redis.clients:jedis")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
