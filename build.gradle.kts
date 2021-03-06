plugins {
    kotlin("jvm") version "1.5.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.digitalpetri.modbus:modbus-slave-tcp:1.2.0")
    implementation("io.insert-koin:koin-core:3.1.0")

    // Database
    implementation("org.hibernate:hibernate-core:5.5.0.Final")
    implementation("mysql:mysql-connector-java:8.0.25")
    implementation("org.hibernate:hibernate-hikaricp:5.5.0.Final")

    // Bean validation
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation ("org.hibernate.validator:hibernate-validator:7.0.1.Final")

    // Caching
    implementation("org.hibernate:hibernate-jcache:5.5.0.Final")
    implementation("org.ehcache:ehcache:3.9.4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.insert-koin:koin-test:3.1.0")
    testImplementation("io.insert-koin:koin-test-junit5:3.1.0")
    testImplementation("io.mockk:mockk:1.11.0")
    testImplementation("com.h2database:h2:1.4.200")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register<Jar>("uberJar") {
    archiveClassifier.set("uber")

    manifest {
        attributes(
            "Main-Class" to "MainKt",
            "Implementation-Title" to "Gradle",
            "Implementation-Version" to archiveVersion
        )
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar")
        }.map {
            zipTree(it)
        }
    })
}
