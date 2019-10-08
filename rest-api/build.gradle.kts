import org.springframework.boot.gradle.tasks.bundling.BootJar

val versions = (extensions.getByName("versions") as Map<String, String>)

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")

dependencies {
    compile(project(":data-model"))
    compile(project(":service-api-model"))

    compile(group = "com.fasterxml.uuid", name = "java-uuid-generator", version = versions["uuidGenerator"])
    compile(group = "org.springframework.boot", name = "spring-boot-starter-web", version = versions["springBoot"])
    compile(group = "com.h2database", name = "h2", version = versions["h2"])
}

tasks.getByName<BootJar>("bootJar") {
    baseName = "rest-user-service"
    version = rootProject.version.toString()
}