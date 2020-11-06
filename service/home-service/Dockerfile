FROM gradle:6.6.1-jdk11 as setup-stage

WORKDIR /app

COPY build.gradle.kts /app/build.gradle.kts
COPY settings.gradle.kts /app/settings.gradle.kts
COPY gradle /app/gradle
COPY src /app/src
COPY piper.p12 /app/src/main/resources/piper.p12

RUN gradle build

FROM openjdk:11 as develop-stage

COPY --from=setup-stage /app/build/libs/piper-home-service.jar /piper-home-service.jar
COPY /deploy/application-dev.yml application.yml

EXPOSE 80

CMD java -jar /piper-home-service.jar

FROM openjdk:11 as production-stage

COPY --from=setup-stage /app/build/libs/piper-home-service.jar /piper-home-service.jar
COPY /deploy/application-dev.yml application.yml

EXPOSE 80

CMD java -jar /piper-home-service.jar
