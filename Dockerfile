FROM openjdk:8

# copy the packaged jar file into our docker image
COPY java/target/external-auth-1.0-SNAPSHOT.jar /external-auth-1.0.jar
 
# set the startup command to execute the jar
CMD ["java", "-jar", "/external-auth-1.0.jar"]
