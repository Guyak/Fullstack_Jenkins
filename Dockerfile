FROM openjdk:19
COPY --chown=gradle:gradle . /home/gradle/src
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp/covid-api
ENTRYPOINT ["./gradlew", "build"]

