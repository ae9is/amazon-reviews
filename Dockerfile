FROM public.ecr.aws/lambda/python:3.12 AS build

ENV TASK_ROOT=/var/task
WORKDIR "${TASK_ROOT}"

# Python setup
RUN python3.12 -m ensurepip
RUN python3.12 -m pip install --no-cache-dir --disable-pip-version-check -U awslambdaric

# Copy model files.
# ARG MODEL_DIR does not persist in final image and does not override MODEL_DIR in env file.
ARG MODEL_DIR=${MODEL_DIR:-data/models/blair-roberta-base}
COPY "${MODEL_DIR}"/* ./model/

# Project dependencies
ARG TORCH_VERSION=${TORCH_VERSION:-cpu}
COPY requirements.prod.${TORCH_VERSION}.txt ./
RUN python3.12 -m pip install --no-cache-dir --disable-pip-version-check -U -r requirements.prod.${TORCH_VERSION}.txt

# Copy project source
COPY src/main/python/amazonrev/*.py ./
COPY src/main/python/amazonrev/lib/*.py ./lib/

# Copy non-secret environment variables.
# Note: docker just silently fails to create hidden files on the container (using COPY, RUN cp, RUN mv, etc...).
COPY .env.dockerfile ./env

ENTRYPOINT ["python3.12", "-m", "awslambdaric"]
CMD ["app.handler"]
