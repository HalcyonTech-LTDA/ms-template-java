plugins {
    java
    jacoco
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.findVersion("java").get().requiredVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Xlint:all",
            "-Xlint:-processing",
            "-Xlint:-serial"
        )
    )
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            targets.all {
                testTask.configure {
                    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
                    testLogging {
                        events("passed", "skipped", "failed", "standard_error")
                        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                        showExceptions = true
                        showCauses = true
                        showStackTraces = true
                    }
                }
            }
        }

        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(project())
            }
            targets.all {
                testTask.configure {
                    description = "Runs integration tests using Testcontainers."
                    group = "verification"
                    shouldRunAfter(test)
                    maxParallelForks = 1
                    testLogging {
                        events("passed", "skipped", "failed", "standard_error")
                        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                        showExceptions = true
                        showCauses = true
                        showStackTraces = true
                    }
                }
            }
        }
    }
}

configurations.named("integrationTestImplementation") {
    extendsFrom(configurations.testImplementation.get())
}
configurations.named("integrationTestRuntimeOnly") {
    extendsFrom(configurations.testRuntimeOnly.get())
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    "mockitoAgent"(platform(libs.findLibrary("spring-boot-bom").get()))
    "mockitoAgent"("org.mockito:mockito-core") {
        isTransitive = false
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs("-Xshare:off")
    jvmArgumentProviders.add(MockitoAgentProvider(mockitoAgent))
}

class MockitoAgentProvider(
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    val agentFiles: FileCollection
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> {
        val jar = agentFiles.singleOrNull()
        return if (jar != null) listOf("-javaagent:${jar.absolutePath}") else emptyList()
    }
}

