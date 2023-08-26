FROM maven:3-amazoncorretto-17 as develop-stage-gateway
WORKDIR /resources

COPY /api/gestsuite-gateway .
RUN mvn clean package -f pom.xml
ENTRYPOINT ["mvn","spring-boot:run","-f","pom.xml"]

FROM maven:3-amazoncorretto-17 as build-stage-gateway
WORKDIR /resources

COPY /api/gestsuite-gateway .
RUN mvn clean package -f pom.xml

FROM amazoncorretto:17-alpine-jdk as production-stage-gateway
COPY --from=build-stage-gateway /resources/target/gateway-0.0.1-SNAPSHOT.jar gateway.jar
COPY /config/ /resources/
ENTRYPOINT ["java","-jar","/gateway.jar"]