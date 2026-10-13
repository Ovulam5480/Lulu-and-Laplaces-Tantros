plugins{
    `java-library`
}

// The root project's `allprojects` block forces `src` as the only source directory,
// this module keeps the standard Gradle/Maven layout instead.
sourceSets["main"].java.setSrcDirs(listOf(layout.projectDirectory.dir("src/main/java")))
sourceSets["test"].java.setSrcDirs(listOf(layout.projectDirectory.dir("src/test/java")))

dependencies{
    compileOnly("com.google.auto.service:auto-service:1.0.1")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")
}

tasks.withType<JavaCompile>().configureEach{
    options.apply{
        encoding = "UTF-8"

        // These processors reach into `jdk.compiler` internals, hence the compiler needs to be forked
        // with the relevant packages exported.
        isFork = true
        // NOTE: `getJvmArgs()` is nullable since Gradle 9 (JSpecify annotations + strict Kotlin
        // nullability), so assigning through the setter instead of mutating the returned list.
        forkOptions.jvmArgs = listOf(
            "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
            "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
            "--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
            "--add-exports", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
            "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
            "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
        )
    }

    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.named<Jar>("jar"){
    archiveFileName = "${project.name}.jar"
}
