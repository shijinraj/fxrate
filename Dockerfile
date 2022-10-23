FROM adoptopenjdk/openjdk11:alpine-jre

# Refer to Maven build -> finalName
ARG JAR_FILE=target/fxrate-0.0.1-SNAPSHOT.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]

#Please use the following commands to run Docker
# docker build -t fxrate:1.0 .
# sudo docker run -d -p 8080:8080 -t fxrate:1.0
# docker ps
# docker stop ${CONTAINER ID}
