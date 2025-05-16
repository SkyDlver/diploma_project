#!/bin/bash

# Start backend
echo "Starting backend..."
mvn --projects backend spring-boot:run &

# Wait for backend to start
echo "Waiting for backend to start..."
sleep 10

# Start frontend
echo "Starting frontend..."
cd frontend
npm run dev

# Kill background processes when script is terminated
trap "trap - SIGTERM && kill -- -$$" SIGINT SIGTERM EXIT 