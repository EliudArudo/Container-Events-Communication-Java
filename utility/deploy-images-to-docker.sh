# docker build -t eliudarudo/java-events-communication-consuming-frontend:dev -f ../consumingfrontend/Dockerfile ../consumingfrontend
# docker build -t eliudarudo/java-events-communication-events-service:dev -f ../eventservice/Dockerfile ../eventservice
# docker build -t eliudarudo/java-events-communication-consuming-backend:dev -f ../consumingbackend/Dockerfile ../consumingbackend

# docker push eliudarudo/java-events-communication-consuming-frontend:dev
# docker push eliudarudo/java-events-communication-events-service:dev
# docker push eliudarudo/java-events-communication-consuming-backend:dev

export COLLECTIVE_VERSION=v1.0.0

docker build -t eliudarudo/java-events-communication-consuming-frontend:$COLLECTIVE_VERSION -f ../consumingfrontend/Dockerfile ../consumingfrontend
docker build -t eliudarudo/java-events-communication-events-service:$COLLECTIVE_VERSION -f ../eventservice/Dockerfile ../eventservice
docker build -t eliudarudo/java-events-communication-consuming-backend:$COLLECTIVE_VERSION -f ../consumingbackend/Dockerfile ../consumingbackend

docker push eliudarudo/java-events-communication-consuming-frontend:$COLLECTIVE_VERSION
docker push eliudarudo/java-events-communication-events-service:$COLLECTIVE_VERSION
docker push eliudarudo/java-events-communication-consuming-backend:$COLLECTIVE_VERSION

docker push eliudarudo/java-events-communication-consuming-frontend:latest
docker push eliudarudo/java-events-communication-events-service:latest
docker push eliudarudo/java-events-communication-consuming-backend:latest