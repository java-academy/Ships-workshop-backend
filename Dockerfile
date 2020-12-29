FROM openjdk:11
COPY target/RoomService-*.jar /app.jar
CMD ["java","-jar","-Dserver.port=$PORT","/app.jar"]
