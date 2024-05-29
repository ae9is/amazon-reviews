GRADLE := ./gradlew
NAME := amazonreviews
GRAPHQL_API_IMAGE_ID = $(shell docker image ls | grep graphql | awk '{print $$3}' | head -n 1)
MODEL_API_IMAGE_ID = $(shell docker image ls | grep model | awk '{print $$3}' | head -n 1)
DB_PROCESS_ID = $(shell docker ps | grep postgres | awk '{print $$1}' | head -n 1)
TESTDB_PROCESS_ID = $(shell docker ps | grep reviews-pg-test | awk '{print $$1}' | head -n 1)

clean:
	${GRADLE} clean

test-env-up:
	docker compose -f docker-compose-test.yml up -d 
	bash docker-db-up.sh reviews-pg-test 1 1

test-env-down:
	bash docker-db-down.sh reviews-pg-test 1 1
	docker compose -f docker-compose-test.yml down

test-py:
	MODEL_API_URL=http://localhost:5001 pdm test

test-java:
	SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/reviews MODEL_API_URL=http://localhost:5001 ${GRADLE} test --rerun-tasks

test: test-env-up test-java test-py test-env-down

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

docker-build: docker-build-java docker-build-py

docker-build-java:
	${GRADLE} bootBuildImage --imageName=${NAME}/graphql-api

docker-build-py:
	printf "PYTHON_ENV=${PYTHON_ENV}\nMODEL_DIR=./model\n" > .env.dockerfile
	docker build -t ${NAME}/model-api --build-arg MODEL_DIR=${MODEL_DIR} --build-arg TORCH_VERSION=${TORCH_VERSION} -f Dockerfile .

docker-login:
	aws ecr get-login-password --region ${AWS_REGION} --profile ${AWS_PROFILE} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

docker-bash-db:
	docker exec --user postgres -it ${DB_PROCESS_ID} /bin/bash

docker-bash-testdb:
	docker exec --user postgres -it ${TESTDB_PROCESS_ID} /bin/bash

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
