GRADLE := ./gradlew
NAME := amazonreviews
API_IMAGE_ID = $(shell docker image ls | grep api | awk '{print $$3}' | head -n 1)
DB_PROCESS_ID = $(shell docker ps | grep postgres | awk '{print $$1}' | head -n 1)

clean:
	${GRADLE} clean

test:
	${GRADLE} test

build:
	${GRADLE} build

run:
	${GRADLE} bootRun

deps:
	${GRADLE} dependencies

docker-build:
	${GRADLE} bootBuildImage --imageName=${NAME}/api

#docker-build:
#	docker build -t ${NAME}/api -f Dockerfile .

docker-login:
	aws ecr get-login-password --region ${AWS_REGION} --profile ${AWS_PROFILE} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

docker-bash-db:
	docker exec --user postgres -it ${DB_PROCESS_ID} /bin/bash

docker-bash-api:
	docker run -it --entrypoint /bin/bash ${API_IMAGE_ID}

docker-tag:
	docker tag ${API_IMAGE_ID} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/api:latest

docker-push:
	docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/api:latest
