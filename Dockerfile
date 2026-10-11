# syntax=docker/dockerfile:1.7

# ==============================================================================
# Stage 1: Builder
# ==============================================================================
FROM eclipse-temurin:26-jdk AS builder

WORKDIR /workspace

# Copy Gradle build descriptors, wrapper, and composite build-logic
COPY gradlew settings.gradle.kts gradle.properties ./
COPY gradle/ gradle/
COPY build-logic/ build-logic/
COPY app/build.gradle.kts app/build.gradle.kts

RUN chmod +x gradlew

# Prefetch app dependencies using BuildKit cache mount
RUN --mount=type=cache,target=/root/.gradle ./gradlew :app:dependencies --no-daemon

# Copy source code and build the production executable Spring Boot fat JAR
COPY app/src app/src

RUN --mount=type=cache,target=/root/.gradle ./gradlew :app:bootJar -x test --no-daemon

# ==============================================================================
# Stage 2: Hardened Production Runtime
# ==============================================================================
FROM eclipse-temurin:26-jre AS runner

# Install curl for container health checks, apply security updates, and remove unused Canonical Pebble binary
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends curl && \
    rm -f /usr/bin/pebble && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Configure timezone and mandatory JVM runtime options
ENV TZ=UTC \
    JAVA_TOOL_OPTIONS="-Duser.timezone=UTC"

# Create dedicated non-root system group and user (UID/GID 1001)
RUN groupadd --system --gid 1001 appgroup && \
    useradd --system --uid 1001 --gid appgroup --no-create-home --shell /bin/false appuser

WORKDIR /app

# Copy built executable JAR and entrypoint script with non-root ownership
COPY --from=builder --chown=appuser:appgroup /workspace/app/build/libs/app.jar /app/app.jar
COPY --chown=appuser:appgroup entrypoint.sh /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh

# Enforce least-privilege security principle: run as non-root user
USER appuser

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["-jar", "/app/app.jar", "--spring.profiles.active=api"]
