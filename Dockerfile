FROM public.ecr.aws/lambda/python:3.12 as build
# No python 3.12 yet in AL2023, so we use the Lambda team's image for now. 
# ref: https://github.com/amazonlinux/amazon-linux-2023/issues/483
# To actually run on Lambda or other platforms without CUDA, swap out GPU torch for CPU-only.
#FROM public.ecr.aws/amazonlinux/amazonlinux:2023 as build

ENV TASK_ROOT=/var/task

# Non-root user and group (only with AL2023 not Lambda base images)
#RUN dnf install -y shadow-utils
#RUN groupadd -g 888 python && useradd -r -u 888 -g python python
#RUN mkdir -p "${TASK_ROOT}"
#RUN chown python:python "${TASK_ROOT}"
#WORKDIR "${TASK_ROOT}"

# Python and wsgi server
#RUN dnf install -y python3.12
#RUN dnf clean all
#USER 888
RUN python3.12 -m venv "${TASK_ROOT}"
ENV PATH="${TASK_ROOT}/bin:${PATH}"
RUN source "${TASK_ROOT}/bin/activate"
RUN python3.12 -m ensurepip
RUN python3.12 -m pip install --no-cache-dir --disable-pip-version-check -U gunicorn uvicorn[standard]

# Copy model files.
# ARG MODEL_DIR does not persist in final image and does not override MODEL_DIR in env file.
ARG MODEL_DIR=${MODEL_DIR:-data/models/blair-roberta-base}
COPY --chown=python:python "${MODEL_DIR}"/* ./amazonrev/model/

# Project dependencies
ARG TORCH_VERSION=${TORCH_VERSION:-cpu}
COPY --chown=python:python requirements.prod.${TORCH_VERSION}.txt ./
RUN python3.12 -m pip install --no-cache-dir --disable-pip-version-check -U -r requirements.prod.${TORCH_VERSION}.txt

# Copy project source
COPY --chown=python:python src/main/python/amazonrev/*.py ./amazonrev/
COPY --chown=python:python src/main/python/amazonrev/lib/*.py ./amazonrev/lib/

# Copy non-secret environment variables.
# Note: docker just silently fails to create hidden files on the container (using COPY, RUN cp, RUN mv, etc...).
COPY --chown=python:python .env.dockerfile ./amazonrev/env

ENV NUM_WORKERS=1
ENV GUNICORN_CMD_ARGS="--bind :5000 --workers=${NUM_WORKERS} --timeout 0"
EXPOSE 5000
ENTRYPOINT ["gunicorn", "--worker-class", "uvicorn.workers.UvicornWorker", "--chdir", "amazonrev", "app:app"]
# Note: uvicorn workers are async, so we disable --timeout
# ref: https://docs.gunicorn.org/en/stable/settings.html#timeout
CMD ["${GUNICORN_CMD_ARGS}"]
HEALTHCHECK --start-period=5s CMD curl -f http://localhost:5000/v1/healthz
