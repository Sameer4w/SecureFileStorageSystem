FROM eclipse-temurin:22-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar

EXPOSE 8080

ENTRYPOINT ["java","-jar","build/libs/SecureFileStorageSystem-0.0.1-SNAPSHOT.jar"]