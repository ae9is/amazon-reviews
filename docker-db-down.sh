#!/bin/bash
# Export and drop database tables.
# Executes against a running docker postgres container.
container="${1:-postgres}"
with_test_data=$2
force=$3
DB_PROCESS_ID=`docker ps | grep "${container}" | awk '{print $1}' | head -n 1`

if [ "${force}" ]; then
  echo "Creating and loading database \"${POSTGRES_DB}\" at Docker process \"${DB_PROCESS_ID}\" ..."
else
  read -p "Really drop database \"${POSTGRES_DB}\" at Docker process \"${DB_PROCESS_ID}\" (y/N)?" choice
  case "${choice}" in
    y|Y ) echo "Continuing...";;
    n|N|* ) echo "Aborting!"; exit 0;;
  esac
fi

if [ "${with_test_data}" ]; then
  export_dir="src/test/resources/database/export"
else
  export_dir="data/export"
fi

# Make sure to fix permissions on your mounted volume if docker is run as root
cp migrations/*.sql "${export_dir}" || { echo "Cannot copy new migrations to docker mount point, quitting!"; exit 1; }

started=`date`
# Migrations run in reverse
for scriptpath in `ls -r migrations/*.down.sql`; do
  script=`basename "${scriptpath}"`
  echo "Running script: ${script} at `date`..."
  docker exec --user postgres ${DB_PROCESS_ID} psql -d ${POSTGRES_DB} -f "/export/${script}"
done
stopped=`date`
echo "Started at: ${started}"
echo "Stopped at: ${stopped}"
