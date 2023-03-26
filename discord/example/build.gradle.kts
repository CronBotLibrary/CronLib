plugins {
    id("java")
}

group = "jp.cron.cronlib"
version = "UNKNOWN"

repositories {
    mavenCentral()
    maven(url = "https://m2.chew.pro/snapshots")
}

dependencies {
    implementation(project(":app"))
    implementation("pw.chew:jda-chewtils:2.0-SNAPSHOT")
    implementation("net.dv8tion:JDA:5.0.0-beta.6")
}