FROM openjdk:17-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY build/libs/careercraft-0.0.1-SNAPSHOT.jar /app/careercraft-0.0.1-SNAPSHOT.jar

CMD ["sh", "-c", "java -jar careercraft-0.0.1-SNAPSHOT.jar"]