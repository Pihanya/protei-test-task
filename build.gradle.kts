buildscript {
    apply(plugin = "java")
    apply(plugin = "idea")

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.1.9.RELEASE")
    }
}

plugins {
    id("com.gradle.build-scan") version ("2.0.2")
    id("org.springframework.boot") version ("2.0.5.RELEASE") apply (false)
}

group = "ru.protei"
version = "1.0.0-SNAPSHOT"

val versions = mutableMapOf(
    Pair("springData", "2.1.10.RELEASE"),
    Pair("springBoot", "2.1.9.RELEASE"),
    Pair("spring", "5.2.0.RELEASE"),
    Pair("javax.annotation", "1.3.2"),
    Pair("javax.persistence", "2.2"),
    Pair("hibernate", "5.3.12.Final"),
    Pair("postgresSQL", "42.2.6"),
    Pair("jackson", "2.10.0"),
    Pair("guava", "28.0-jre"),
    Pair("uuidGenerator", "3.2.0"),
    Pair("log4J", "2.12.1"),
    Pair("h2", "1.4.199"),
    Pair("mockito", "1.10.19"),
    Pair("junitJupyter", "5.4.2"),
    Pair("junitPlatform", "1.5.2"),
    Pair("lombok", "1.18.10")
)

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https=//plugins.gradle.org/m2/")
    }
    extensions.add("versions", versions)
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")

    dependencies {
        implementation(group = "org.springframework.boot", name = "spring-boot-dependencies", version = versions["springBoot"])
        compile(group = "com.google.guava", name = "guava", version = versions["guava"])

        compileOnly(group = "org.projectlombok", name = "lombok", version = versions["lombok"])
        annotationProcessor(group = "org.projectlombok", name = "lombok", version = versions["lombok"])

        testCompile(group = "org.mockito", name = "mockito-all", version = versions["mockito"])
//        testCompile(group = "org.junit.jupiter", name = "junit-jupiter", version = versions["junitJupyter"])
        testCompile(group = "org.junit.jupiter", name = "junit-jupiter-params", version = "5.0.0")
        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.0.0")
        testRuntime(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.0.0")
//        testRuntime(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = versions["junitJupyter"])
    }

    project.buildDir = file("${rootProject.buildDir}/${project.name}")
    idea.module {
        iml.generateTo = file("${rootProject.projectDir}/.idea")

        sourceSets["main"].java.srcDir("$projectDir/src/main/java")
        sourceSets["test"].java.srcDir("$projectDir/src/test/java")

        resourceDirs.addAll(listOf(
            file("$projectDir/src/main/resources"),
            file("$projectDir/src/test/resources")
        ))

        generatedSourceDirs.add(file("$buildDir/generated"))

        isDownloadJavadoc = true
        isDownloadSources = true
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType(JavaCompile::class) {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc>() {
        options.encoding = "UTF-8"
    }

    tasks.getByName<Test>("test") {
        systemProperties["file.encoding"] = "UTF-8"
        useJUnitPlatform()
    }
}