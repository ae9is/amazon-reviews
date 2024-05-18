FROM public.ecr.aws/lambda/python:3.12 as build
# No python 3.12 yet in AL2023, so we use the Lambda team's image for now. 
# ref: https://github.com/amazonlinux/amazon-linux-2023/issues/483
# However, note to actually run on Lambda would need to swap out GPU torch for CPU-only in pyproject.toml.
#FROM public.ecr.aws/amazonlinux/amazonlinux:2023 as build

# Non-root user and group (only with AL2023 not Lambda base images)
#RUN dnf install -y shadow-utils
#RUN groupadd -g 888 python && useradd -r -u 888 -g python python
#ENV TASK_ROOT=/var/task
#RUN mkdir -p "${TASK_ROOT}"
#RUN chown python:python "${TASK_ROOT}"
#WORKDIR "${TASK_ROOT}"

# Python and wsgi server
#RUN dnf install -y python3.12
#RUN dnf clean all
#USER 888
RUN python3.12 -m venv "${TASK_ROOT}"
ENV PATH="${TASK_ROOT}/bin:${PATH}"
RUN python3.12 -m ensurepip
RUN python3.12 -m pip install --no-cache-dir --disable-pip-version-check -U gunicorn uvicorn[standard]

# Copy model files.
# ARG MODEL_DIR does not persist in final image and does not override MODEL_DIR in env file.
ARG MODEL_DIR=${MODEL_DIR:-data/models/blair-roberta-base}
COPY --chown=python:python "${MODEL_DIR}"/* ./amazonrev/model/

# Project dependencies
COPY --chown=python:python requirements.prod.txt ./
RUN python3.12 -m pip install --no-cache-dir --disable-pip-version-check -U -r requirements.prod.txt

# Copy project source
COPY --chown=python:python src/main/python/amazonrev/*.py ./amazonrev/
COPY --chown=python:python src/main/python/amazonrev/lib/*.py ./amazonrev/lib/

# Copy non-secret environment variables.
# Note: docker just silently fails to create hidden files on the container (using COPY, RUN cp, RUN mv, etc...).
COPY --chown=python:python .env.dockerfile ./amazonrev/env

EXPOSE 5000
ENTRYPOINT ["gunicorn", "--worker-class", "uvicorn.workers.UvicornWorker", "--chdir", "amazonrev", "app:app"]
# Note: uvicorn workers are async, so we disable --timeout
# ref: https://docs.gunicorn.org/en/stable/settings.html#timeout
CMD ["--bind", ":5000", "--workers=4", "--timeout", "0"]
HEALTHCHECK --start-period=5s CMD curl -f http://localhost:5000/v1/healthz
