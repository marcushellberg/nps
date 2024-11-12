#!/bin/bash

# Exit on any error
set -e

echo "🚀 Starting local deployment..."

# Check if we're in the right directory (should have pom.xml)
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: pom.xml not found. Are you in the project root directory?"
    exit 1
fi

echo "📦 Building with Maven..."
if ! mvn clean package -Pproduction; then
    echo "❌ Maven build failed"
    exit 1
fi

echo "🐳 Building Docker image..."
if ! docker build -t nps:local . -f Dockerfile.local; then
    echo "❌ Docker build failed"
    exit 1
fi

echo "☸️  Applying Kubernetes configuration..."
if ! kubectl apply -k k8s/base; then
    echo "❌ Kubernetes configuration failed"
    exit 1
fi

echo "🔄 Restarting application pod..."
# Get the deployment pod name
POD_NAME=$(kubectl get pod -n nps -l app=nps -o jsonpath="{.items[0].metadata.name}")

if [ -n "$POD_NAME" ]; then
    echo "📝 Found pod: $POD_NAME"
    kubectl delete pod -n nps "$POD_NAME"
    echo "⏳ Waiting for new pod to be ready..."
    kubectl wait --namespace nps \
        --for=condition=ready pod \
        --selector=app=nps \
        --timeout=60s
    echo "✅ New pod is ready!"
else
    echo "⚠️  No existing pod found, skipping restart"
fi

# Final status check
echo "📊 Checking deployment status..."
kubectl get pods -n nps

echo "✅ Deployment complete! Your app should be available at http://nps.lvh.me"