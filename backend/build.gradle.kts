plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named("bootRun") {
    dependsOn(":frontend:webCopyFrontend")
}


tasks.named("bootJar") {
    dependsOn(":frontend:webCopyFrontend")
}

tasks.named("build") {
    dependsOn(":frontend:webCopyFrontend")
}
