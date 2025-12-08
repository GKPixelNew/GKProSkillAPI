plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.citizensnpcs.co/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.gkpixel.com/public/")
    maven("https://repo.aikar.co/nexus/content/groups/aikar/")
    maven("https://repo.md-5.net/content/groups/public/")
    maven("https://repo.travja.dev/releases")
    maven("https://repo.travja.dev/snapshots")
}
var codex_version = "1.1.1-R0.16-SNAPSHOT"

dependencies {
    testImplementation("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.48.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.17.0")
    testImplementation("org.slf4j:slf4j-reload4j:2.0.17")
    testImplementation("commons-io:commons-io:2.19.0")
    testImplementation("io.netty:netty-all:4.2.1.Final")
    testImplementation("studio.magemonkey:codex:${codex_version}") {
        exclude(group = "com.sk89q.worldedit")
        exclude(group = "com.sk89q.worldguard")
    }

    api("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("ru.endlesscode.mimic:mimic-bukkit-api:0.8.0")
    compileOnly("org.projectlombok:lombok:1.18.38")
    compileOnly("me.libraryaddict.disguises:libsdisguises:11.0.0") {
        exclude(group = "org.spigotmc", module = "spigot-api")
        exclude(group = "org.ow2.asm", module = "asm")
    }
    compileOnly("com.github.retrooper:packetevents-spigot:2.8.0")
    compileOnly("fr.neatmonster:nocheatplus:3.16.0-RC")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13-SNAPSHOT") {
        exclude(group = "com.sk89q.worldedit")
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.11-SNAPSHOT") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    implementation("studio.magemonkey:codex:${codex_version}") {
        exclude(group = "com.sk89q.worldedit")
        exclude(group = "com.sk89q.worldguard")
    }
    compileOnly("com.mojang:authlib:3.11.50")
    compileOnly("dev.robothanzo.gk.replay:GKReplay:1.8.2-SNAPSHOT")
    compileOnly("io.lumine:LumineUtils:1.21-SNAPSHOT")
    compileOnly("io.lumine:Mythic:5.11.1")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
}

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}

group = "dev.robothanzo.gk.sapi"
version = "1.3.2-R0.3-SNAPSHOT"
description = "GKProSkillAPI"
java.sourceCompatibility = JavaVersion.VERSION_23

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(24)
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
            environment("FABLED_VERSION", version)
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