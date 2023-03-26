plugins {
    id("java")
}

group = "jp.cron.cronlib"
version = System.getenv("VERSION") ?: "UNKNOWN"

repositories {
    mavenCentral()
}

dependencies {
}
