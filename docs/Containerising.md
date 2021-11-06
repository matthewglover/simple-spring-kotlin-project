# Example containerising and pushing an image

## Pre-requisites

1. AWS CLI
2. Docker
3. AWS user with sufficient permissions, including `ACCESS_KEY_ID` and `SECRET_ACCESS_KEY`

```shell
## Configure AWS CLI with user
export AWS_ACCESS_KEY_ID="<AWS_ACCESS_KEY_ID>"
export AWS_SECRET_ACCESS_KEY="<AWS_SECRET_ACCESS_KEY>"

# Create ECR Repository, which will return the ecr repository uri to be used in future commands
aws ecr create-repository --repository-name simple-project --region eu-west-1

# Build image directly with ecr repo for image name (note: this avoids creating image then tagging again)
./gradlew bootBuildImage --imageName= <ecr-repository-uri>:<image-tag>

# Login to ECR and configure docker
aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin <ecr-repository-uri>

# Push the image to ECR
docker image push <ecr-repository-uri>:<image-tag>
```
