plugins {
    `java-library`
}

val versions = (extensions.getByName("versions") as Map<String, String>)

dependencies {
    compile(group = "javax.annotation", name = "javax.annotation-api", version = versions["javax.annotation"])
    compile(group = "javax.persistence", name = "javax.persistence-api", version = versions["javax.persistence"])

    compile(group = "org.springframework.boot", name = "spring-boot-starter-data-jpa", version = versions["springBoot"])
    compile(group = "org.postgresql", name = "postgresql", version = versions["postgresSQL"])

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test", version = versions["springBoot"])
    testCompile(group = "com.h2database", name = "h2", version = versions["h2"])
}