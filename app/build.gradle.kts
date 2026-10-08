plugins {
    alias(libs.plugins.spring.boot)
    id("template.java-conventions")
    id("template.spotless-conventions")
    id("template.quality-conventions")
    id("template.sbom-conventions")
}

springBoot {
    mainClass.set(project.findProperty("mainClass")?.toString() ?: "com.example.templatejava.ApiApplication")
}

dependencies {
    // Platform BOMs (Gradle Native BOM Resolution)
    implementation(platform(libs.spring.boot.bom))
    implementation(platform(libs.spring.cloud.bom))

    // Production Dependencies
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation(libs.spring.boot.starter.opentelemetry)
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.spring.cloud.starter.circuitbreaker.resilience4j)
    implementation(libs.springdoc.openapi)
    implementation(libs.apache.commons.lang3)
    implementation(libs.apache.commons.collections4)
    implementation(libs.shedlock.mongo)
    implementation(libs.shedlock.spring)

    constraints {
        implementation(libs.tomcat.embed.core)
        implementation(libs.tomcat.embed.el)
        implementation(libs.tomcat.embed.websocket)
        implementation(libs.jackson.databind.patch)
        implementation(libs.jackson.tools.patch)
    }

    // Testing Dependencies
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.spring.boot.starter.actuator.test)
    testImplementation(libs.spring.boot.starter.data.mongodb.test)
    testImplementation(libs.spring.boot.starter.opentelemetry.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.grafana)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.mongodb)
    testImplementation(libs.testcontainers.mockserver)
    testImplementation(libs.mockserver.client)
    testImplementation(libs.rest.assured)
    testImplementation(libs.awaitility)
    testImplementation(libs.archunit.junit5)
    testRuntimeOnly(libs.junit.platform.launcher)
}
