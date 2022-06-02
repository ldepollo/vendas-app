FROM openjdk:17

COPY ./target/vendas-app-0.0.1-SNAPSHOT.jar vendas-app-0.0.1-SNAPSHOT.jar

CMD ["java","-jar","vendas-app-0.0.1-SNAPSHOT.jar"]