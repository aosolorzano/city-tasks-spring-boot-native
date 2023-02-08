#####################################################################################
########################## Stage 1: Docker Builder Image ############################
#####################################################################################
FROM ghcr.io/graalvm/graalvm-ce:22.3.1 AS builder
COPY mvnw     /workspace/app/mvnw
COPY .mvn     /workspace/app/.mvn
COPY pom.xml  /workspace/app/pom.xml

WORKDIR /workspace/app
RUN gu install native-image && \
    ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

COPY src /workspace/app/src
RUN ./mvnw clean -Pnative native:compile -DskipTests

#####################################################################################
#################### Stage 2: Docker Native Application Image #######################
#####################################################################################
FROM oraclelinux:9-slim
COPY --from=builder /workspace/app/target/city-tasks-spring-native application

EXPOSE 8080
CMD ["sh", "-c", "./application"]
