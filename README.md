# Simple Project

## What

A very simple Spring Boot application built using Gradle, Kotlin, Co-routines and Reactor, demonstrating basic
configuration, setup and testing.

## Static Analysis

The project uses the following static analysis tools:

1. [Detekt](https://detekt.github.io/detekt/) - for code quality rule enforcement
2. [KtLint](https://ktlint.github.io/) - for code linting and auto-formatting

Both Detekt and KtLint will be run as part of the `check` build phase.

To run the tasks individually:

`./gradlew detekt` - runs detekt code analysis

`./gradlew ktlintCheck` - runs ktlint formatting checks

`./gradlew ktlintFormat` - run ktlint auto-formatting

## Dependency Management

The project uses the following dependency management tools:

1. [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) - for managing version upgrades
2. [Dependency Check Plugin](https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html) - for
   checking for dependencies against OWASP vulnerabilities database

To run tasks:

`./gradlew dependencyUpdates` - checks for updates to current specified versions

`./gradlew dependencyCheckAnalyze` - checks dependencies for vulnerabilities

## Testing

The project contains multiple test sources:

- `handlerTest`: for testing routing and handler logic in isolation
- `test`: for unit testing

Each test source can be run in isolation with command: `./gradlew <test-souce>`, e.g. `./gradlew test`
or `./gradlew handlerTest`

## Check task

The check task is configured to run:

- Detekt
- KtLint
- All test sources (`test`, `handlerTest`)

## Building and running a containerised image

`./gradlew bootBuildImage --imageName=<image-name>:<image-tag>` - builds a containerised image.

`docker run -it -p 8090:8080 <image-name>:<image-tag>` - runs containerised image on port 8090

## Pushing a containerised image to ECR

1. Create ECR repository name of docker image: `aws ecr create-repository --repository-name <image-name>`
2. Tag the image with ECR repository uri: `docker image tag <image-name>:<image-tag> <ecr-repository-uri>:<image-tag>`
3. Login to ECR
   - `aws ecr get-login-password --region <ecr-region> | docker login --username AWS --password-stdin <ecr-repository-uri>`
4. Push the image to ECR: `docker image push <ecr-repository-uri>:<image-tag>`

See [here](docs/Containerising.md) for a full example of building and pushing an image to ECR

## Running in Kubernetes

See [here](docs/Local_Kubernetes.md) for how to set up and run local Kubernetes cluster.
