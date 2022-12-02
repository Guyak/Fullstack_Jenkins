FROM openjdk:19
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp/covid-api
RUN chmod 555 gradlew
RUN ./gradlew build



