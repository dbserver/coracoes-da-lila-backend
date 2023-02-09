FROM maven:3.8.7-openjdk-18-slim AS build
WORKDIR /app
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

RUN mv /home/app/target/ProjetoLilaBack-0.0.1-SNAPSHOT.jar /app/ProjetoLilaBack.jar
# EXPOSE 8090
ENTRYPOINT ["java","-jar","/app/ProjetoLilaBack.jar"]