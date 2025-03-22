import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
    id("org.liquibase.gradle") version "2.2.0"
    id("nu.studer.jooq") version "10.0"
}

group = "com.htcmw"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springGrpcVersion"] = "0.3.0"
val jooqVersion = "3.19.19" // 안정된 동일 버전 사용
val postgresqlVersion = "42.7.3"
dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Spring Boot(WebFlux, Actuator, JOOQ, R2DBC)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // graphql
    implementation("org.springframework.boot:spring-boot-starter-graphql")

    // gRPC
    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
    implementation("io.grpc:grpc-services")

    //liquibase
    implementation("org.liquibase:liquibase-core")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
    liquibaseRuntime("org.liquibase:liquibase-core:4.20.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresqlVersion")

    //jooq
    implementation("org.jooq:jooq:$jooqVersion")
    jooqGenerator("org.jooq:jooq-codegen:$jooqVersion")
    jooqGenerator("org.postgresql:postgresql:$postgresqlVersion")

    // DB
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    // Test
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.grpc:spring-grpc-test")
    testImplementation("org.springframework.graphql:spring-graphql-test")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    option("jakarta_omit")
                    option("@generated=omit")
                }
            }
        }
    }
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
            "changeLogFile" to "db/db-changelog-master.yaml",
            "url" to "jdbc:postgresql://localhost:5432/rebac",
            "username" to "postgres",
            "password" to "postgres",
            "driver" to "org.postgresql.Driver",
            "searchPath" to "src/main/resources"
        )
    }
    runList = "main"
}

jooq {
    version.set(jooqVersion)
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN

                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/rebac"
                    user = "postgres"
                    password = "postgres"
                }

                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        excludes = "databasechangelog|databasechangeloglock"
                    }
                    generate.apply {
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.htcmw.rebac.jooq"
                        directory = "src/generated/jooq" // 생성 위치
                    }
                }
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
