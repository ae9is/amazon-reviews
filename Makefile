GRADLE := ./gradlew
NAME := amazonreviews
GRAPHQL_API_IMAGE_ID = $(shell docker image ls | grep graphql | awk '{print $$3}' | head -n 1)
MODEL_API_IMAGE_ID = $(shell docker image ls | grep model | awk '{print $$3}' | head -n 1)
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

parse:
	${GRADLE} runParser

embeddings:
	pdm parser

docker-build:
	${GRADLE} bootBuildImage --imageName=${NAME}/graphql-api
	printf "PYTHON_ENV=${PYTHON_ENV}\nMODEL_DIR=./model\n" > .env.dockerfile
	docker build -t ${NAME}/model-api --build-arg MODEL_DIR=${MODEL_DIR} -f Dockerfile .

docker-login:
	aws ecr get-login-password --region ${AWS_REGION} --profile ${AWS_PROFILE} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

docker-bash-db:
	docker exec --user postgres -it ${DB_PROCESS_ID} /bin/bash

docker-bash-graphql-api:
	docker run -it --entrypoint /bin/bash ${GRAPHQL_API_IMAGE_ID}

docker-bash-model-api:
	docker run -it --entrypoint /bin/bash ${MODEL_API_IMAGE_ID}

docker-tag:
	docker tag ${GRAPHQL_API_IMAGE_ID} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/graphql-api:latest
	docker tag ${MODEL_API_IMAGE_ID} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/model-api:latest

docker-push:
	docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/graphql-api:latest
	docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/model-api:latest
