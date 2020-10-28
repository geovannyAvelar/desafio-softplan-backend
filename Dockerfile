FROM openjdk:11 as build

COPY gradlew .
COPY .gradle .gradle
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN ./gradlew clean bootJar

FROM openjdk:11
ARG DEPENDENCY=/build/libs/cadastro-pessoas-0.0.1-SNAPSHOT.jar
COPY --from=build ${DEPENDENCY} cadastro-pessoas.jar
EXPOSE 8080
ENTRYPOINT java -jar -Dspring.profiles.active=homologacao cadastro-pessoas.jar
