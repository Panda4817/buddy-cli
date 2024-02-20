# Buddy CLI

Helpful terminal buddy with useful and fun commands, created using Spring Shell.

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Shell
- Maven
- GraalVM

## Pre-requisites

The CLI uses ChatGPT and Vertex AI. 

You will need a ChatGPT API key and a Google Cloud project with Vertex AI enabled.

You will need to set these variables in the terminal:
```
$ export VERTEX_AI_PROJECT_ID=<your Google Cloud project ID>
$ export VERTEX_AI_LOCATION=<your Google Cloud Vertex AI location>
$ export CHATGPT_KEY=<your ChatGPT API key>
```

You will need to log in to your Google Cloud account using the `gcloud` CLI:
```
$ gcloud auth application-default login
$ gcloud config set project <your Google Cloud project ID>  && gcloud auth login <your account email>
```

## Maven

To build jar and run tests:

```
$ mvn clean install
```

To run with spring-boot:

```
$ mvn spring-boot:run
```

To run with jar:

```
$ java -jar target/buddy-0.0.1.jar
```


## GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Lightweight Container with Cloud Native Buildpacks
If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```
$ mvn spring-boot:build-image -Pnative
```

Then, you can run the app like any other container:

```
$ docker run --rm -p 8080:8080 buddy:0.0.1
```

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ mvn native:compile -Pnative
```

Then, you can run the app as follows:
```
$ target/buddy
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

***NOTE: Mockito is not supported yet***

To run your existing tests in a native image, run the following goal:

```
$ mvn test -PnativeTest
```

