# =====================================================
# Multi-Stage Dockerfile for Spring Boot Application
# Siguiendo mejores prácticas oficiales 2025
# =====================================================

# ==================== STAGE 1: Build ====================
# Usa Maven con Eclipse Temurin JDK 17 para compilar
FROM eclipse-temurin:17-jdk-jammy AS builder

# Metadata
LABEL description="Spring Boot Notes App - Build Stage"

# Instalar Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Configurar directorio de trabajo
WORKDIR /app

# Si solo cambia el código (no pom.xml), Docker reutiliza esta capa
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (skip tests para build más rápido)
# -DskipTests: salta tests en build (ejecutarlos en CI/CD)
# -B: batch mode (sin output interactivo)
RUN mvn clean package -DskipTests -B

# Verificar que el JAR fue creado
RUN ls -lah /app/target/

# ==================== STAGE 2: Extract Layers ====================
# Extraer layers del JAR para optimizar imágenes
FROM eclipse-temurin:17-jdk-jammy AS extractor

WORKDIR /app

# Copiar JAR desde stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Extraer layers (Spring Boot 2.3+)
# Esto permite que Docker cachee capas que no cambian (dependencies)
RUN java -Djarmode=layertools -jar app.jar extract

# ==================== STAGE 3: Runtime ====================
# Imagen final super ligera con solo el JRE
FROM eclipse-temurin:17-jre-jammy

# Metadata
LABEL description="Spring Boot Notes App - Production Image"

# SEGURIDAD: Crear usuario non-root
RUN groupadd -r spring && useradd -r -g spring spring

# Configurar directorio de trabajo
WORKDIR /app

# Copiar layers en orden (de menos a más cambiante)
# Docker cachea cada capa - las que no cambian se reutilizan
COPY --from=extractor --chown=spring:spring /app/dependencies/ ./
COPY --from=extractor --chown=spring:spring /app/spring-boot-loader/ ./
COPY --from=extractor --chown=spring:spring /app/snapshot-dependencies/ ./
COPY --from=extractor --chown=spring:spring /app/application/ ./

# Cambiar a usuario non-root
USER spring:spring

# Exponer puerto (documentativo - no abre el puerto)
EXPOSE 80

# JVM Optimizations para contenedores
# -XX:+UseContainerSupport: detecta límites de memoria del container
# -XX:MaxRAMPercentage=75.0: usa máximo 75% de RAM disponible
# -Djava.security.egd: mejora performance de SecureRandom
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
