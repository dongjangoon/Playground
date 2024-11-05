plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":core"))

    // API 특화 의존성이 필요한 경우 여기에 추가
}

tasks {
    bootJar {
        archiveBaseName.set("nickname-api")
        archiveVersion.set(version.toString())
        enabled = true
    }

    jar {
        enabled = false
    }
}
