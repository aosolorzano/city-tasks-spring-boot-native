
## Spring Native with WebFlux, Quartz, TDD, and Testcontainers.

* **Author**: [Andres Solorzano](https://www.linkedin.com/in/aosolorzano/).
* **Level**: Advanced.
* **Technologies**: Java, Spring Boot, Spring Native, Spring WebFlux, Quartz, Flyway, Testcontainers, Postgres, DynamoDB and Docker.

---

## Description
This project uses the Spring Boot Framework to perform CRUD operations over Tasks records that store Quartz Jobs on a Postgres database.
The idea is to use Reactive Programing with the help of the Spring WebFlux library, and a TDD methodology from the beginning with the support of Testcontainers to execute the Integration Tests.
Also, the project uses Spring Native to compile the application into a native executable that runs on a Docker container alongside the other services in a Docker cluster.

## GraalVM Native Support
This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Running the application using Docker Compose
Execute the following command from the root of the project:
```bash
docker compose up --build
```

### Getting a Device item from DynamoDB from LocalStack
Execute the following command:
```bash
aws dynamodb scan         \
  --table-name Devices    \
  --endpoint-url http://localhost:4566
```

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ ./mvnw native:compile -Pnative
```

Then, you can run the app as follows:
```
$ target/city-tasks-api
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```
$ ./mvnw test -PnativeTest
```

## Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/#build-image)
* [GraalVM Native Image Support](https://docs.spring.io/spring-boot/docs/3.0.2/reference/html/native-image.html#native-image)
* [Testcontainers Postgres Module Reference Guide](https://www.testcontainers.org/modules/databases/postgres/)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#web.reactive)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Testcontainers](https://www.testcontainers.org/)
* [Quartz Scheduler](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#io.quartz)
* [Flyway Migration](https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/#howto.data-initialization.migration-tool.flyway)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Additional Links
These additional references should also help you:

* [Configure AOT settings in Build Plugin](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/htmlsingle/#aot)
