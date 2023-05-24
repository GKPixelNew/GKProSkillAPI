plugins {
    java
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.md-5.net/content/repositories/snapshots/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.md-5.net/content/groups/public/")
    maven("https://repo.gkpixel.com/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("LibsDisguises:LibsDisguises:10.0.30")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    compileOnly("fr.neatmonster:nocheatplus:3.16.1-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.0.3-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.3")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.1.0")
    compileOnly("com.promcteam:promccore:1.0.4-R0.5-SNAPSHOT")
    compileOnly("com.promcteam:proskillapiparties:1.1.0.2-SNAPSHOT")
    compileOnly("ru.endlesscode.mimic:mimic-bukkit-api:0.8.0")
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("dev.robothanzo.gk.replay:GKReplay:1.8.2-SNAPSHOT")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:2.138.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.mockito:mockito-inline:4.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.0")
    testImplementation("org.slf4j:slf4j-reload4j:2.0.7")
    testImplementation("commons-io:commons-io:2.11.0")
}

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}

group = "mc.promcteam"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    processResources {
        outputs.upToDateWhen { false }

        filesMatching("**/plugin.yml") {
            filter {
                it.replace("%project.version%", version as String)
            }
        }
    }
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
