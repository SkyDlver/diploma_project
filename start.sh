#!/bin/bash
set -e  # Exit immediately if a command exits with a non-zero status

# Check for prerequisites
command -v bun >/dev/null 2>&1 || { echo "Bun is required but not installed. Aborting."; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo "Maven is required but not installed. Aborting."; exit 1; }

# Project root directory
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Start the backend
echo "Starting Spring Boot backend..."
cd "$ROOT_DIR/backend" || { echo "Backend directory not found!"; exit 1; }
mvn spring-boot:run &
BACKEND_PID=$!

# Wait for backend to start
echo "Waiting for backend to start..."
MAX_ATTEMPTS=30
ATTEMPT=0
while ! curl -s http://localhost:8098/api/health >/dev/null 2>&1; do
  ATTEMPT=$((ATTEMPT+1))
  if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "Backend failed to start within the expected time!"
    kill $BACKEND_PID
    exit 1
  fi
  echo "Waiting for backend... ($ATTEMPT/$MAX_ATTEMPTS)"
  sleep 1
done

# Start the frontend
echo "Starting Vue.js frontend..."
cd "$ROOT_DIR/frontend" || { echo "Frontend directory not found!"; exit 1; }
bun run serve &
FRONTEND_PID=$!

# Handle termination
cleanup() {
  echo "Shutting down services..."
  kill -15 $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
  wait $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
  echo "All services stopped."
}

trap cleanup SIGINT SIGTERM EXIT

# Keep the script running
wait $BACKEND_PID $FRONTEND_PID