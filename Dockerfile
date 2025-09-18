FROM gradle:8.7-jdk21-alpine AS build

WORKDIR /app

COPY . .

RUN gradle clean bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
