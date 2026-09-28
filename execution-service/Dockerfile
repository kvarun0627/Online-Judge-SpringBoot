# --- Build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Runtime stage ---
# Full JDK (not JRE) because this container itself compiles Java submissions
# via javac - see PythonExecutor/JavaExecutor/CppExecutor, which shell out to
# these toolchains directly since DockerSandboxRunner is currently disabled
# (execution.sandbox.disabled: true).
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    g++ \
    python3 \
    && ln -s /usr/bin/python3 /usr/local/bin/python \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
