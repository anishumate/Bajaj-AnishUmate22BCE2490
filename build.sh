#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if command -v mvn >/dev/null 2>&1; then
  mvn -DskipTests package
else
  echo "mvn not found locally — building with docker maven image (requires docker)."
  docker run --rm -v "$PWD":/workspace -w /workspace maven:3.8.8-openjdk-17-slim mvn -DskipTests package
fi

echo "Built jar at target/bajaj-finserv-qualifier-0.1.0.jar"
