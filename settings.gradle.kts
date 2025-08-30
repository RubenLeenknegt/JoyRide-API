plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "fantastic-lamp"
include("backend")
include("app")     // optioneel
include("shared")  // optioneel
