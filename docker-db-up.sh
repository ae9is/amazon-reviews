#!/bin/bash
# Create and load database tables.
# Executes against a running docker postgres container.
# Make sure to generate loadable data files first by running `make parse`.
container="${1:-postgres}"
with_test_data=$2
force=$3
DB_PROCESS_ID=`docker ps | grep "${container}" | awk '{print $1}' | head -n 1`

if [ "${force}" ]; then
  echo "Creating and loading database \"${POSTGRES_DB}\" at Docker process \"${DB_PROCESS_ID}\" ..."
else
  read -p "Really create and load database \"${POSTGRES_DB}\" at Docker process \"${DB_PROCESS_ID}\" (y/N)?" choice
  case "${choice}" in
    y|Y ) echo "Continuing ...";;
    n|N|* ) echo "Aborting!"; exit 0;;
  esac
fi

if [ "${with_test_data}" ]; then
  echo "Using test data directories ..."
  import_dir="src/test/resources/database/import"
  export_dir="src/test/resources/database/export"
else
  import_dir="data/import"
  export_dir="data/export"
fi

# Make sure to fix permissions on your mounted volume if docker is run as root
cp migrations/*.sql "${export_dir}" || { echo "Cannot copy new migrations to docker mount point, quitting!"; exit 1; }
if [ "${with_test_data}" ]; then
  # Handle test-only database creation differences
  rename --force 's/.test.sql/.sql/' "${export_dir}"/*.test.sql
fi

if [ "${force}" ]; then
  wipe=1
else
  read -p "Wipe and replace data (y/N)?" wipe
fi
if [ "${wipe}" ]; then
  GROUP=`id -gn`
  chown "${USER}" "${import_dir}"/*.csv && chgrp "${GROUP}" "${import_dir}"/*.csv || echo "Warning: Cannot fix data file permissions"
  rsync -a "${import_dir}"/*.csv "${export_dir}"
fi

started=`date`
for scriptpath in migrations/*.up.sql; do
  script=`basename "${scriptpath}"`
  echo "Running script: ${script} at `date`..."
  docker exec --user postgres ${DB_PROCESS_ID} psql -d ${POSTGRES_DB} -f "/export/${script}"
done
stopped=`date`
echo "Started at: ${started}"
echo "Stopped at: ${stopped}"
