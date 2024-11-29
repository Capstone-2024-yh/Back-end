plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "com.capstone"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate:hibernate-spatial:6.2.8.Final") // 공간 관련 기능 유지
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.github.cdimascio:dotenv-java:2.2.4")
    implementation("org.locationtech.jts:jts-core:1.18.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.postgis:postgis-jdbc:1.3.3")

    implementation("org.hibernate:hibernate-core:6.4.0.Final") // Hibernate 6.4로 업그레이드
    implementation("org.hibernate.orm:hibernate-vector:6.4.0.Final") // Hibernate 벡터 모듈 추가

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4") // 코루틴 사용

    implementation("com.github.haifengl:smile-core:3.0.0")
    implementation("com.github.haifengl:smile-data:2.6.0")   // DenseMatrix가 포함된 모듈
    implementation("com.github.haifengl:smile-math:2.6.0")    // 수학 연산을 위한 모듈

    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
    jvmToolchain(21)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
