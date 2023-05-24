FROM maven:3-amazoncorretto-11 as develop-stage-gateway
COPY . .

RUN mvn clean package -f pom.xml
ENTRYPOINT ["mvn","spring-boot:run","-f","pom.xml"]

FROM maven:3-amazoncorretto-11 as build-stage-gateway
WORKDIR /resources
COPY . .
RUN mvn clean compile install package -f pom.xml

FROM amazoncorretto:11-alpine-jdk as production-stage-gateway
COPY --from=build-stage-gateway /resources/target/gateway-0.0.1-SNAPSHOT.jar gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar"]