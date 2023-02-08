
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

*NOTE:* GraalVM 22.3+ is required.

### Running the application using Docker Compose
First, generate the Spring Native image:
```bash
./mvnw clean -Pnative native:compile
```

Then, execute the following command from the root of the project:
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

## Using the Executable only
Use this option if you want to explore more options such as running your tests in a native image.
*IMPORTANT:* The GraalVM `native-image` compiler should be installed and configured on your machine.

Deploy the required services using Docker Compose command:
```bash
docker compose up tasks-postgres tasks-localstack
```

Open a new terminal window and export the following environment variables:
```bash
export HIPERIUM_CITY_TASKS_DB_CLUSTER_SECRET='{"dbClusterIdentifier":"hiperium-city-tasks-db-cluster","password":"postgres123","dbname":"HiperiumCityTasksDB","engine":"postgres","port":5432,"host":"localhost","username":"postgres"}'
export AWS_DEFAULT_REGION=ap-southeast-2
export AWS_ACCESS_KEY_ID=DUMMY
export AWS_SECRET_ACCESS_KEY=DUMMY
export AWS_ENDPOINT_OVERRIDE=http://localhost:4566
```

Then, create and run the native executable from the project's root directory:
```bash
$ ./mvnw clean native:compile -Pnative spring-boot:run
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:
```bash
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
