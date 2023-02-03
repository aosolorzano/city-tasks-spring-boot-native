#####################################################################################
########################## Stage 1: Docker Builder Image ############################
#####################################################################################
FROM amazoncorretto:17.0.6-al2022-RC as build
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw clean package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

#####################################################################################
######################## Stage 2: Docker Application Image ##########################
#####################################################################################
FROM amazoncorretto:17.0.6-al2022-RC-headless
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib     /app/lib
COPY --from=build ${DEPENDENCY}/META-INF         /app/META-INF
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.hiperium.city.tasks.api.TasksApplication"]
