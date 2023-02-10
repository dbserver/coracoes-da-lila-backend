FROM eclipse-temurin:17-alpine
ADD ./target/ProjetoLilaBack-0.0.1-SNAPSHOT.jar ProjetoLilaBack.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","./ProjetoLilaBack.jar"]