plugins {
    java
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.citizensnpcs.co/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://repo.gkpixel.com/public/")
}
var codex_version = "1.0.0-R0.18-SNAPSHOT"

dependencies {
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.86.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
    testImplementation("org.slf4j:slf4j-reload4j:2.0.13")
    testImplementation("commons-io:commons-io:2.16.1")
    testImplementation("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    testImplementation("io.netty:netty-all:4.1.109.Final")
    testImplementation("studio.magemonkey:codex:${codex_version}")
    implementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("ru.endlesscode.mimic:mimic-bukkit-api:0.8.0")
    compileOnly("org.projectlombok:lombok:1.18.32")
    compileOnly("LibsDisguises:LibsDisguises:10.0.44") {
        exclude(group = "org.spigotmc", module = "spigot-api")
        exclude(group = "org.ow2.asm", module = "asm")
    }
    compileOnly("fr.neatmonster:nocheatplus:3.16.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    compileOnly("com.sk89q.worldedit:worldedit-core:7.1.0-SNAPSHOT") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    compileOnly("studio.magemonkey:codex:${codex_version}")
    compileOnly("com.mojang:authlib:3.11.50")
    compileOnly("dev.robothanzo.gk.replay:GKReplay:1.8.2-SNAPSHOT")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
}

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}

group = "dev.robothanzo.gk.sapi"
version = "1.3.2-R0.3-SNAPSHOT"
description = "GKProSkillAPI"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    processResources {
        filesMatching("**/paper-plugin.yml") {
            expand("project" to project)
        }
        filesMatching("**/plugin.yml") {
            expand("project" to project)
        }
    }

    test {
        doFirst {
            environment("CODEX_VERSION", codex_version)
            environment("FABLED_VERSION", project.version)
        }
        useJUnitPlatform()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
    repositories {
        maven {
            name = "Reposilite"
            url = uri(
                (if ((publications.getByName("maven") as MavenPublication).version.toString()
                        .endsWith("-SNAPSHOT")
                )
                    System.getenv("REPOSILITE_HOST") + "/snapshots" else System.getenv("REPOSILITE_HOST") + "/releases")
            )
            credentials {
                username = System.getenv("REPOSILITE_USER")
                password = System.getenv("REPOSILITE_PASSWORD")
            }
        }
    }
}