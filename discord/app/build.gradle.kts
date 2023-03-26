plugins {
    application
    id("java")
}

group = "jp.cron.cronlib"
version = "UNKNOWN"

repositories {
    mavenCentral()
    maven(url = "https://m2.chew.pro/snapshots")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.6")
    implementation("pw.chew:jda-chewtils:2.0-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.yaml:snakeyaml:2.0")

    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    implementation("org.json:json:20230227")
    implementation("net.sf.trove4j:trove4j:3.0.3")
    annotationProcessor("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

}

application {
    mainClass.set("jp.cron.cronlib.Main")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
                .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}