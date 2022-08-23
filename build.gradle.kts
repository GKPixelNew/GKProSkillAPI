plugins {
    java
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.md-5.net/content/repositories/snapshots/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.md-5.net/content/groups/public/")
    maven("https://maven.gkpixel.com/repository/anon-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("LibsDisguises:LibsDisguises:10.0.30")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    compileOnly("fr.neatmonster:nocheatplus:3.16.1-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.0.3-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.3")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.1.0")
    compileOnly("com.github.promcteam:promccore:dev-SNAPSHOT")
    compileOnly("com.github.promcteam:proskillapiparties:master-SNAPSHOT")
    compileOnly("ru.endlesscode.mimic:mimic-bukkit-api:0.8.0")
    compileOnly("org.projectlombok:lombok:1.18.24")
    compileOnly("dev.robothanzo.gk:GKReplay:1.8.2")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

group = "mc.promcteam"
version = "1.1.7.18"
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
                it.replace("{project.version}", version as String)
            }
        }
    }
}
