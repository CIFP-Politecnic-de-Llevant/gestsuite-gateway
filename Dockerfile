FROM maven:3-amazoncorretto-11 as develop-stage-gateway
COPY . .
RUN mvn clean install -f pom.xml
ENTRYPOINT ["mvn","spring-boot:run"]

FROM maven:3-amazoncorretto-11 as build-stage-gateway
WORKDIR /resources
COPY . .
RUN mvn clean compile install package -f pom.xml

FROM amazoncorretto:11-alpine-jdk as production-stage-gateway
COPY --from=build-stage-gateway /resources/target/eureka-server-0.0.1-SNAPSHOT.jar eureka.jar
ENTRYPOINT ["java","-jar","/eureka.jar"]