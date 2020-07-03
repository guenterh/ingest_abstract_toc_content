FROM hseeberger/scala-sbt:8u252_1.3.12_2.12.11 AS build

# Preliminary build
WORKDIR /build
RUN sbt new scala/scala-seed.g8 --name=build -o . && rm -r src/test
COPY build.sbt .
COPY project/ ./project/
RUN sbt compile

ADD src/ ./src

RUN sbt assembly

FROM openjdk:8-jre

#COPY --from=build /build/target/scala-2.12/*.jar /app/
COPY --from=build /build/target/scala-2.12/build-assembly-0.1.0-SNAPSHOT.jar /app/

CMD java -cp  /app/build-assembly-0.1.0-SNAPSHOT.jar Main
