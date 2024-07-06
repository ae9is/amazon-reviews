GRADLE := ./gradlew
NAME := amazonreviews
GRAPHQL_API_IMAGE_ID = $(shell docker image ls | grep graphql | awk '{print $$3}' | head -n 1)
MODEL_API_IMAGE_ID = $(shell docker image ls | grep model | awk '{print $$3}' | head -n 1)
DB_PROCESS_ID = $(shell docker ps | grep postgres | awk '{print $$1}' | head -n 1)
TESTDB_PROCESS_ID = $(shell docker ps | grep reviews-pg-test | awk '{print $$1}' | head -n 1)

clean:
	${GRADLE} clean

# A small delay between bringing up docker compose services and executing the database scripts
#  is needed for CI workflow. (Docker compose exits before the containers are fully up.)
test-env-up:
	docker compose -f docker-compose-test.yml up -d 
	sleep 5
	bash docker-db-up.sh reviews-pg-test 1 1

test-env-down:
	bash docker-db-down.sh reviews-pg-test 1 1
	docker compose -f docker-compose-test.yml down

test-py: test-env-up
	MODEL_API_URL=http://localhost:5001 pdm test

test-java: test-env-up
	SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/reviews MODEL_API_URL=http://localhost:5001 ${GRADLE} test --rerun-tasks

test-ci: test-java test-py

# In make v4.4+ can just replace this with .WAIT
test: test-env-up test-ci WAIT test-env-down
WAIT: test-java test-py

build:
	${GRADLE} build

run:
	${GRADLE} bootRun

deps:
	${GRADLE} dependencies
	pdm install

get-model:
	pdm get-model

parse:
	${GRADLE} runParser

embeddings:
	pdm parser

docker-build: docker-build-java docker-build-py

docker-build-java:
	${GRADLE} bootBuildImage --imageName=${NAME}/graphql-api

docker-build-py: prep-docker-env
	docker build -t ${NAME}/model-api --build-arg MODEL_DIR=${MODEL_DIR} --build-arg TORCH_VERSION=${TORCH_VERSION} -f Dockerfile .

prep-docker-env:
	printf "PYTHON_ENV=${PYTHON_ENV}\nMODEL_DIR=./model\nUSE_QUANTIZATION=${USE_QUANTIZATION}\nHF_HOME=/tmp\n" > .env.dockerfile

sam-build-py: prep-docker-env
	sam build

docker-login:
	aws ecr get-login-password --region ${AWS_REGION} --profile ${AWS_PROFILE} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

docker-login-github:
	echo ${GITHUB_TOKEN} | docker login --username ${GITHUB_REPOSITORY_OWNER} --password-stdin ghcr.io

docker-bash-db:
	docker exec --user postgres -it ${DB_PROCESS_ID} /bin/bash

docker-bash-testdb:
	docker exec --user postgres -it ${TESTDB_PROCESS_ID} /bin/bash

docker-bash-graphql-api:
	docker run -it --entrypoint /bin/bash ${GRAPHQL_API_IMAGE_ID}

docker-bash-model-api:
	docker run -it --entrypoint /bin/bash ${MODEL_API_IMAGE_ID}

docker-tag:
#	docker tag ${GRAPHQL_API_IMAGE_ID} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/graphql-api:${RELEASE_TAG}
#	docker tag ${MODEL_API_IMAGE_ID} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/model-api:${RELEASE_TAG}
	docker tag ${GRAPHQL_API_IMAGE_ID} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/graphql-api:latest
	docker tag ${MODEL_API_IMAGE_ID} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/model-api:latest

docker-tag-github:
	docker tag ${GRAPHQL_API_IMAGE_ID} ghcr.io/${GITHUB_REPOSITORY_OWNER}/${NAME}-graphql-api:${RELEASE_TAG}
	docker tag ${MODEL_API_IMAGE_ID} ghcr.io/${GITHUB_REPOSITORY_OWNER}/${NAME}-model-api:${RELEASE_TAG}
	docker tag ${GRAPHQL_API_IMAGE_ID} ghcr.io/${GITHUB_REPOSITORY_OWNER}/${NAME}-graphql-api:latest
	docker tag ${MODEL_API_IMAGE_ID} ghcr.io/${GITHUB_REPOSITORY_OWNER}/${NAME}-model-api:latest

docker-push:
#	docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/graphql-api:${RELEASE_TAG}
#	docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/model-api:${RELEASE_TAG}
	docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/graphql-api:latest
	docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${NAME}/model-api:latest

docker-push-github:
	docker push ghcr.io/${GITHUB_REPOSITORY_OWNER}/${NAME}-graphql-api:${RELEASE_TAG}
	docker push ghcr.io/${GITHUB_REPOSITORY_OWNER}/${NAME}-model-api:${RELEASE_TAG}
	docker push ghcr.io/${GITHUB_REPOSITORY_OWNER}/${NAME}-graphql-api:latest
	docker push ghcr.io/${GITHUB_REPOSITORY_OWNER}/${NAME}-model-api:latest
