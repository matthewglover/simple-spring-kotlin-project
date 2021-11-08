# Running in a local Kubernetes cluster

## Pre-requisites

To run locally, you will need the following:

- AWS CLI
- Docker
- k3d
- helm

## Configure k3d cluster

Set up a k3d cluster, configured to pull images from ECR:

#### Configure ECR Registry mirror

Create a `registries.yaml` with following content:

```yaml
mirrors:
  <ecr-registry>:
    endpoint:
      - https://<ecr-registry>

configs:
  <ecr-registry>:
    auth:
      username: AWS
      password: <ecr-password>
```

Password can be retrieved: `aws ecr get login-password --region <ecr-region> | pbcopy`

#### Create a k3d cluster

To bind to port `80`, run: `k3d cluster create simple-cluster --registry-config path/to/registries.yaml -p "80:80@loadbalancer"`

## Install application

#### Configure Helm to connect to your ECR repo

Create a file at location `./helm/simple-project/values/local.yaml`, with contents:

```yaml
registry: "<ECR_REGISTRY>"
```

#### Install application
From project root run: 
```bash
helm install simple-project \
--values ./helm/simple-project/values/local.yaml \
--set imageTag=<IMAGE_TAG> \
./helm/simple-project
```


