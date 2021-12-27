plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.spring") version "1.5.30"
    kotlin("plugin.jpa") version "1.5.30"
    id("java-library")
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.springframework.boot") version "2.6.0"
    id("maven-publish")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
//springBoot {
//    buildInfo()
//}

allprojects {
    repositories {
        mavenCentral()
    }
}

group = "org.kate"
version = "0.1.0"

val jarArtifact = artifacts.add("archives", file("build/libs/kate-$version.jar")) {
    type = "jar"
    builtBy("jar")
}

publishing {
    publications {
        create<MavenPublication>("Kate") {
            artifact(jarArtifact)
            pom {
                name.set("Kate library")
                description.set("KAfka requesT/ response framework with Events")
                url.set("https://github.com/keesbroenink/Kate")

                licenses {
                    license {
                        name.set("GNU")
                        url.set("https://www.gnu.org/licenses/")
                    }
                }
                developers {
                    developer {
                        id.set("keesbroenink")
                        name.set("Kees Broenink")
                        email.set("keesbroenink@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/keesbroenink/kate")
                }

            }
        }
    }
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springdoc:springdoc-openapi-ui:1.5.12")
    testImplementation("org.springdoc:springdoc-openapi-kotlin:1.5.12")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
    archiveClassifier.value("")
}




