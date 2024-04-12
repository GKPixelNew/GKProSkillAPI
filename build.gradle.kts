plugins {
    java
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.citizensnpcs.co/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.gkpixel.com/public/")
}

dependencies {
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.77.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("org.slf4j:slf4j-reload4j:2.0.7")
    testImplementation("commons-io:commons-io:2.11.0")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("io.lumine:Mythic-Dist:5.2.0")
    compileOnly("ru.endlesscode.mimic:mimic-bukkit-api:0.8.0")
    compileOnly("org.projectlombok:lombok:1.18.32")
    compileOnly("LibsDisguises:LibsDisguises:10.0.21")
    compileOnly("fr.neatmonster:nocheatplus:3.16.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.1.0-SNAPSHOT")
    compileOnly("studio.magemonkey:codex:1.0.0-R0.1-SNAPSHOT")
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

group = "com.promcteam"
version = "1.3.2-R0.3-SNAPSHOT"
description = "ProSkillAPI"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
//
//    processResources {
//        outputs.upToDateWhen { false }
////
////        filesMatching("**/plugin.yml") {
////            filter {
////                it.replace("%project.version%", version as String)
////            }
////        }
//    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "mc.promcteam"
            artifactId = "proskillapi"
            version = version

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
}