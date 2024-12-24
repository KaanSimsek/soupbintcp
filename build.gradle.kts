import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.jmailen.kotlinter") version "4.4.1"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	//id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
	id("maven-publish")
}

group = "io.soapbintcp"
version = "0.0.1"

java.sourceCompatibility = JavaVersion.VERSION_21


repositories {
	mavenCentral()
	mavenLocal()
}

tasks.getByName<Jar>("jar") {
	archiveClassifier.set("")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("javax.xml.bind:jaxb-api:2.3.1")
	implementation("org.glassfish.jaxb:jaxb-runtime:2.3.3")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = "io.soapbintcp"
			artifactId = "event-based-soap-bin-tcp"
			version = "0.0.1"
			from(components["java"])
		}
	}
}

/*
signing {
	val keyId = System.getenv("SIGNING_KEYID")
	val secretKey = System.getenv("SIGNING_SECRETKEY")
	val passphrase = System.getenv("SIGNING_PASSPHRASE")

	useInMemoryPgpKeys(keyId, secretKey, passphrase)
}

centralPortal {
	username = System.getenv("SONATYPE_USERNAME")
	password = System.getenv("SONATYPE_PASSWORD")

	pom {
		name = "SoapBinTcp"
		description = "This is a helper library for using soap bin tcp protocol on Springboot "
		url = ""
		scm {
			url = "https://github.com/KaanSimsek/soapbintcp/tree/main"
		}

		licenses {
			license {
				name.set("The Apache License, Version 2.0")
				url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
			}
		}

		developers {
			developer {
				id.set("0")
				name.set("KaanSimsek")
				email.set("kaan.simsek01@gmail.com")
			}
		}
	}
}

 */
