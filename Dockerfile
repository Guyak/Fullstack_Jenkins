FROM gradle:4.7.0-jdk8-alpine
COPY --chown=gradle:gradle . /home/gradle/src
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp/covid-api
ENTRYPOINT ["./gradlew", "build"]

