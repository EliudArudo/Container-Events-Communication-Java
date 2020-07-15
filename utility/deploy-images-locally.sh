docker build -t eliudarudo/java-events-communication-consuming-frontend:dev -f ../consumingfrontend/Dockerfile ../consumingfrontend
docker build -t eliudarudo/java-events-communication-events-service:dev -f ../eventservice/Dockerfile ../eventservice
docker build -t eliudarudo/java-events-communication-consuming-backend:dev -f ../consumingbackend/Dockerfile ../consumingbackend
