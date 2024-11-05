plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":core"))

    // Spring Batch
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("com.h2database:h2")

    // Test
    testImplementation("org.springframework.batch:spring-batch-test")

    // JSON 파일 처리를 위한 의존성
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

tasks {
    bootJar {
        archiveBaseName.set("nickname-batch")
        archiveVersion.set(version.toString())
        enabled = true
    }

    jar {
        enabled = false
    }
}
