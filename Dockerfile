FROM openjdk:8-jre-alpine
ADD target/scala-2.12/app.jar /app/app.jar
CMD java -jar /app/app.jar