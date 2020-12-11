FROM openjdk:11
VOLUME /tmp
COPY target/ShipsBackend-*.jar /app.jar
CMD ["java","-jar","-Dserver.port=$PORT","/app.jar"]
