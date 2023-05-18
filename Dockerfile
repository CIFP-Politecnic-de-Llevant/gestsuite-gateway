FROM maven:3-amazoncorretto-11 as build-stage
WORKDIR /resources
COPY . .
#RUN mvn clean compile install package -Pdocker -f pom.xml
RUN mvn clean compile install package -f pom.xml

FROM amazoncorretto:11-alpine-jdk as production-stage
COPY --from=build-stage /resources/target/gateway-0.0.1-SNAPSHOT.jar gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar"]