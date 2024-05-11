# amazon-reviews

Spring GraphQL API based on normalising the [Amazon Reviews 2023 dataset](https://github.com/hyp1231/AmazonReviews2023) in Postgres.

## Data 

See: https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023/tree/main

The data needs to be downloaded and prepped for import into the database.

1. Download the following files to [data/import](data/import):
    - [Metadata (item info) file, Musical Instruments category only](https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023/resolve/main/raw/meta_categories/meta_Musical_Instruments.jsonl?download=true)
    - [Reviews data file, Musical Instruments category only](https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023/resolve/main/raw/review_categories/Musical_Instruments.jsonl?download=true)

2. Then run the following task:

    ```bash
    make parse
    ```

*Note: Feel free to download, merge, and parse the data for **all** of the categories—but it's a lot bigger!*

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

<img src="img/screenshot.png" width=400 />

## Test

With the Postgres database started:

```bash
make test
```

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
