# amazon-reviews

Demo Spring GraphQL API based on normalising the [Amazon Reviews 2023 dataset](https://github.com/hyp1231/AmazonReviews2023) in Postgres.

## Data 

See: https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023/tree/main

Metadata (item info) file:
https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023/resolve/main/raw/meta_categories/meta_Musical_Instruments.jsonl?download=true

Reviews data file:
https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023/resolve/main/raw/review_categories/Musical_Instruments.jsonl?download=true

## Run

To run the Spring api and Postgres database via Docker:

```bash
direnv allow
make docker-build
docker compose up -d
```

Open http://localhost:4000/graphiql?path=/graphql

You can also directly run the Spring api at the same time with:

```bash
make run
```

Open http://localhost:8080/graphiql?path=/graphql

## Database

### Migrations

Bring up the database instance with Docker and then:

```bash
# Create and load tables
bash docker-db-up.sh

# Dump and drop tables
bash docker-db-down.sh
```

### CLI

To get a shell to the running Postgres instance:

```bash
make docker-bash-db
postgres@...$ psql test
test=# \d
```
