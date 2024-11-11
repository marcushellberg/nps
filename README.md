# Spring Boot and Vaadin NPS demo app

## Requirements

- Java 21
- Docker
- API keys as environment variables
    - Google OAuth2
        - `GOOGLE_CLIENT_ID`
        - `GOOGLE_CLIENT_SECRET`

## Running the app locally

Run `NpsApplication` class in your IDE or run `./mvnw spring-boot:run` in the terminal.

This will automatically start a PostgreSQL database through the included docker-compose file and
initialize the database with some sample data.

## Running the app in a local Kubernetes cluster

You need to have a local Kubernetes cluster running. You can use Minikube or Docker Desktop.

Build the Docker image:

```shell
docker build -t nps:local . 
```

Install an nginx ingress controller:

```shell
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx
```

Apply the Kustomization file:

```shell
kubectl apply -k k8s/base
```

## Running the app in a prod Kubernetes cluster

Build and push the Docker image to a container registry:

```shell
docker build -t <tag>:<version> . --push
```

Update the image and domain in the Kustomization file.

Install an nginx ingress controller:

```shell
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx
```

Install cert-manager:

```shell
helm repo add jetstack https://charts.jetstack.io
helm repo update
helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.13.3 \
  --set installCRDs=true
```

Apply the Kustomization file:

```shell
kubectl apply -k k8s/overlays/prod
```