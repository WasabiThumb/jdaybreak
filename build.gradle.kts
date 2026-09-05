plugins {
    id("java-library")
    alias(libs.plugins.indra.base)
    alias(libs.plugins.indra.licenser)
    alias(libs.plugins.indra.publishing)
}

description = "Lightweight Java library for querying the system UI theme"
group = "io.github.wasabithumb"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jspecify)
    api(libs.jetbrains.annotations)
    testImplementation(platform("org.junit:junit-bom:5.10.5"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

indra {
    apache2License()
    github("WasabiThumb", "jdaybreak")
    javaVersions {
        target(8)
        minimumToolchain(25)
    }
    configurePublications {
        artifactId = "jdaybreak"
        pom {
            name = "JDaybreak" // canonical capitalization
            inceptionYear = "2026"
            developers {
                developer {
                    id = "WasabiThumb"
                    url = "https://github.com/WasabiThumb/"
                    name = "Xavier Pedraza"
                    email = "xpedraza542@gmail.com"
                    timezone = "America/New_York"
                    roles = setOf("author", "developer")
                }
            }
        }
    }
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("license_header.txt"))
}


sourceSets.test {
    multirelease {
        alternateVersions(25)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
}
