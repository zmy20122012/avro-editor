# avro-editor

## this is a online avro editor for simple using


### Framework
springboot + thymeleaf + bootstrap

### Usage
1. run app
2. Go to http://localhost:8080

### run in docker
1. build jar
`./gradlew build `
2. build image
`docker build --build-arg JAR_FILE=build/libs/\*.jar -t avro-editor .`
3. run
`docker run -p 8080:8080 avro-editor`

### docker hub
https://hub.docker.com/r/zmy20122012/avro-editor
