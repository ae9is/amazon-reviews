# amazon-reviews

Spring GraphQL API based on normalising the [Amazon Reviews 2023 dataset](https://github.com/hyp1231/AmazonReviews2023) in Postgres.

Also contains a Python API for running the [BLaIR model](https://github.com/hyp1231/AmazonReviews2023/tree/main/blair) for item recommendations.

<img src="img/screenshot-recommendations.png" width=400 />

## Setup

### Requirements

The project has been developed on Linux for Linux-based Docker image deployment. Your mileage may vary with other platforms.

The Python API requires generation of some embeddings which are loaded into the database. This task effectively requires GPU-enabled PyTorch; it's very slow without.

### Environment variables

Setup loading .env variables: https://direnv.net/

```bash
direnv allow
```

### Python

Uses Python 3.12. To easily switch between versions of python, consider setting up [pyenv](https://github.com/pyenv/pyenv).

[PDM](https://github.com/pdm-project/pdm) is used for proper dependency resolution and convenience scripts.

```bash
pip install pipx
pipx install pdm
pdm install
pdm install-cpu
# OR
pdm install-cuda
```

### Java

Uses Java 17+ with Gradle. Gradle is included in the project files already.

```
apt install openjdk-17-jdk
make deps
```

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

3. For the item recommendations API, some item metadata needs to be fed through a model to generate embeddings. This takes a while and is interruptible:

    ```bash
    make embeddings
    ```

*Note: Feel free to download, merge, and parse the data for **all** of the categories—but it's a lot bigger!*

## Run

To run the Spring and Python APIs and Postgres database via Docker:

```bash
direnv allow
make docker-build
docker compose up
```

Open http://localhost:4000/graphiql?path=/graphql

(Optional) You can also directly run the Spring API at the same time with:

```bash
make run
```
Open http://localhost:8080/graphiql?path=/graphql

<img src="img/screenshot-reviews.png" width=400 />

### GPU-accelerated containers

To run the Python API inside a Docker container with CUDA enabled in PyTorch, the container host should setup Docker for CUDA.

See: https://docs.nvidia.com/datacenter/cloud-native/container-toolkit/latest/index.html

## Test

Build the Docker images first using:

```bash
make docker-build
```

Then run:

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
postgres@...:/$ psql reviews
reviews=# \d
```

## Production Demo

### Deployment

To spin up a free live demo using [Render](https://render.com):

1. Issue a release commit which causes the GitHub actions workflow to tag and publish the API Docker images to GitHub Packages.

1. Create Render account.

1. Create two web services for each API (Java GraphQL API, Python model API), and set environment variables as appropriate referring to:
    - [.env.graphql.demo](.env.graphql.demo)
    - [.env.model.demo](.env.model.demo)

1. Create a Postgres database service. Make sure your local Postgres fits well within 1GB and load it into the remote Render database using:

    ```bash
    docker compose up
    make docker-bash-db
    pg_dump --format=custom --no-acl --no-owner --quote-all-identifiers --verbose --file /export/backup.dump --exclude-schema=pg_catalog -h localhost -U postgres reviews 
    # Modify this, inserting the external connection string for your database from Render
    pg_restore --verbose --no-acl --no-owner -d postgres://reviews:supersecretgeneratedpassword@instancesubdomain.region-postgres.render.com/reviews_abcd /export/backup.dump
    ```

### Caveats

- Free tier instances spin down on Render, i.e. it takes a minute after the first request to each API (graphql API, model API) for it to be live again
- `itemSummariesByQuery` uses the model API behind scenes, and will error out for a bit until the model API goes live again
- The model API is quantized for the demo and the recommendation results are somewhat lower quality

### Example queries

#### Example 1

**_Query_**

```gql
query reviewsByAsin($asin: String!, $params: ReviewPaginationInput) {
  reviewsByAsin(asin: $asin, params: $params) {
    cursor
    list {
      asin
      helpfulVote
      id
      images {
        attachmentType
        id
        largeImageURL
        mediumImageURL
        smallImageURL
      }
      parentAsin
      rating
      text
      timestamp
      title
      userID
      verifiedPurchase
    }
  }
}
```

**_Variables_**

```json
{
  "asin": "B0BSGM6CQ9",
  "params": {
  	"limit": 5,
    "sort": "NEWEST"
  }
}
```

#### Example 2

**_Query_**

```gql
query itemSummariesByQuery($queryText: String!, $limit: Int) {
  itemSummariesByQuery(queryText: $queryText, limit: $limit) {
    id
    title
    averageRating
    ratingNumber
    price
    store
    parentAsin
  }
}
```

**_Variables_**

```json
{
  "queryText": "I need a quiet instrument that outputs to a MIDI interface",
  "limit": 5
}
```
