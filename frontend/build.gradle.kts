plugins {
    id("com.github.node-gradle.node") version "7.0.2"
}

node {
    download.set(true)
    version.set("20.14.0")
    npmVersion.set("10.7.0")
}

tasks.named("npmInstall") {
    inputs.files("package.json", "package-lock.json")
    outputs.dir("node_modules")
}

val buildFrontend = tasks.register<com.github.gradle.node.npm.task.NpmTask>("buildFrontend") {
    dependsOn(tasks.named("npmInstall"))
    npmCommand.set(listOf("run", "build"))
    inputs.files("package.json", "package-lock.json", "index.html", fileTree("src"))
    outputs.dir("dist")
    group = "build"
    description = "Builds the Vite frontend"
}

tasks.register("npmBuild") {
    dependsOn(buildFrontend)
}

tasks.register("npmClean") {
    doLast {
        delete("node_modules", "dist")
    }
}

tasks.register<Copy>("webCopyFrontend") {
    dependsOn(buildFrontend)
    from(layout.projectDirectory.dir("dist"))
    into(rootProject.layout.projectDirectory.dir("backend/src/main/resources/static"))
}
