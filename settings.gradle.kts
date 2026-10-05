pluginManagement{
    repositories{
        gradlePluginPortal()
        mavenLocal()
        // Proxied mirrors of the GitHub-hosted repositories below. They must come first: Gradle aborts the whole
        // repository chain on a connection error, so an unreachable `raw.githubusercontent.com` would otherwise
        // prevent the mirrors from ever being tried.
        maven("https://ghproxy.net/https://raw.githubusercontent.com/Ovulam5480/EntityAnnoMaven/main")
        maven("https://ghproxy.net/https://raw.githubusercontent.com/GglLfr/MindustryClientMaven/main")
        maven("https://raw.githubusercontent.com/Ovulam5480/EntityAnnoMaven/main"){
            content{includeGroupByRegex("com\\.github.*")}
        }
        maven("https://raw.githubusercontent.com/GglLfr/MindustryClientMaven/main"){
            content{includeGroupByRegex("com\\.github.*")}
        }
    }

    plugins{
        val entVersion = providers.gradleProperty("entVersion").get()
        val clientVersion = providers.gradleProperty("clientVersion").get()

        id("com.github.Ovulam5480.EntityAnno") version(entVersion)
        id("com.github.GglLfr.MindustryClient") version(clientVersion)
    }
}

if(JavaVersion.current().ordinal < JavaVersion.VERSION_17.ordinal){
    throw IllegalStateException("JDK 17 is a required minimum version. Yours: ${System.getProperty("java.version")}")
}
