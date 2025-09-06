plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.1"
}

group = "me.zimzaza4"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://maven.elytrium.net/repo/")
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {

    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.8.0")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
    implementation("net.elytrium.commons:config:1.2.5-1")
}

tasks {
    jar {
        dependsOn(shadowJar)
    }
}

tasks.test {
    useJUnitPlatform()
}