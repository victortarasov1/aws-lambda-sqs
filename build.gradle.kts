import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "aws.sqs"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.cloud:spring-cloud-function-adapter-aws:4.1.3")
	implementation("com.amazonaws:aws-lambda-java-events:3.14.0")
	implementation("org.springframework.cloud:spring-cloud-function-context")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
val shadowTasks = tasks.withType<ShadowJar> {
	archiveClassifier = "aws"
	mergeServiceFiles()
	append("META-INF/spring.handlers")
	append("META-INF/spring.schemas")
	append("META-INF/spring.tooling")
	append("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")
	append("META-INF/spring/org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration.imports")
	transform(PropertiesFileTransformer::class.java) {
		paths = listOf("META-INF/spring.factories")
		mergeStrategy = "append"
	}
	manifest {
		attributes["Main-Class"] = "aws.sqs.example.ExampleApplication"
	}
}

tasks.named("assemble") {
	dependsOn(shadowTasks)
}