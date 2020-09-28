plugins {
    kotlin("jvm") version "1.4.10"
    id("kr.entree.spigradle") version "2.2.3"
}

group = "kr.sul"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("com.destroystokyo.paper", "paper-api", "1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc", "spigot", "1.12.2-R0.1-SNAPSHOT")
    compileOnly(files("C:/Users/PHR/Desktop/PluginStorage/CustomEntity_SuL.jar"))
    compileOnly(files("C:/Users/PHR/Desktop/PluginStorage/NotFat/ServerCore_S-NotFat.jar"))
}

spigot {
    authors = listOf("SuL")
    apiVersion = "1.12"
    version = project.version.toString()
    softDepends = listOf("CustomEntity", "ServerCore")
    commands {
        create("worldmanager") {
            permission = "op.op"
        }
    }
}

tasks.compileJava.get().options.encoding = "UTF-8"

val fatJar = task("fatJar", type = Jar::class) {
    archiveFileName.set("${project.name}_S.jar")
    destinationDirectory.set(file("C:/Users/PHR/Desktop/PluginStorage"))
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    compileKotlin.get().kotlinOptions.jvmTarget = "1.8"
    compileTestKotlin.get().kotlinOptions.jvmTarget = "1.8"

    jar {
        dependsOn(fatJar)
        archiveFileName.set("${project.name}_S-NotFat.jar")
        destinationDirectory.set(file("C:/Users/PHR/Desktop/PluginStorage/NotFat"))
    }
}
