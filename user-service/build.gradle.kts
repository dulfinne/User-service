import com.google.protobuf.gradle.id

plugins {
  java
  id("org.springframework.boot") version "3.5.4"
  id("io.spring.dependency-management") version "1.1.7"
  id("com.google.protobuf") version "0.9.5"
}

val springCloudVersion by extra("2025.0.0")
val springGrpcVersion by extra("0.11.0")

group = "com.dulfinne.randomgame"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

repositories {
  mavenCentral()
}

val lombokVersion = "1.18.38"
val mapstructVersion = "1.6.3"
val lombokMapstructBindingVersion = "0.2.0"
val assertJVersion = "3.27.3"
val grpcVersion = "1.75.0"
val protocVersion = "3.25.5"
val redisTestContainersVersion = "2.2.2"
val grpcClientVersion = "3.1.0.RELEASE"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.mapstruct:mapstruct:$mapstructVersion")
  implementation("org.springframework.cloud:spring-cloud-starter-config")
  implementation("org.springframework.kafka:spring-kafka")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("io.grpc:grpc-protobuf:$grpcVersion")
  implementation("io.grpc:grpc-stub:$grpcVersion")
  implementation("net.devh:grpc-client-spring-boot-starter:$grpcClientVersion")

  compileOnly("org.projectlombok:lombok:$lombokVersion")
  runtimeOnly("io.grpc:grpc-netty-shaded:$grpcVersion")
  annotationProcessor("org.projectlombok:lombok:$lombokVersion")
  annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
  annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")

  testCompileOnly("org.projectlombok:lombok:$lombokVersion")
  testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.assertj:assertj-core:${assertJVersion}")
  testImplementation("io.rest-assured:rest-assured")
  testImplementation("org.testcontainers:mongodb")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.springframework.kafka:spring-kafka-test")
  testImplementation("org.testcontainers:kafka")
  testImplementation("com.redis:testcontainers-redis:${redisTestContainersVersion}")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:$protocVersion"
  }
  plugins {
    id("grpc") {
      artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
    }
  }
  generateProtoTasks {
    all().forEach {
      it.plugins {
        id("grpc")
      }
    }
  }
}

tasks.withType<JavaCompile> {
  options.annotationProcessorPath = configurations.annotationProcessor.get()
}

tasks.named<JavaCompile>("compileTestJava") {
  options.annotationProcessorPath = configurations.testAnnotationProcessor.get()
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    mavenBom("org.springframework.grpc:spring-grpc-dependencies:$springGrpcVersion")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
