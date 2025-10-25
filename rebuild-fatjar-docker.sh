#!/usr/bin/env bash
set -euo pipefail

# Usage: ./rebuild-fatjar-docker.sh [--skip-tests]
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

SKIP_TESTS=0
for arg in "$@"; do
  case "$arg" in
    --skip-tests) SKIP_TESTS=1 ;;
    *) echo "Unknown arg: $arg"; exit 1 ;;
  esac
done

# prefer project gradlew if present and executable
if [ -x "$SCRIPT_DIR/gradlew" ]; then
  GRADLEW="$SCRIPT_DIR/gradlew"
  echo "Building with gradlew..."
else
  GRADLEW="gradle"
  echo "Building with system gradle..."
fi

build_args=(clean build)
if [ "$SKIP_TESTS" -eq 1 ]; then
  build_args+=(-x test)
fi

"$GRADLEW" "${build_args[@]}"

# run backend-specific build (mirror original script)
backend_args=(clean :backend:build)
if [ "$SKIP_TESTS" -eq 1 ]; then
  backend_args+=(-x test)
fi

"$GRADLEW" "${backend_args[@]}"

echo "==> Bringing down compose stack (remove volumes)..."
docker compose -f docker-compose.local.yml down -v

echo "==> Bringing up compose stack (rebuild images)..."
docker compose -f docker-compose.local.yml up --build -d

echo "==> Done."
