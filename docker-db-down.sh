#!/bin/bash
# Export and drop database tables.
# Executes against a running docker postgres container.
DB_PROCESS_ID=`docker ps | grep postgres | awk '{print $1}' | head -n 1`

read -p "Really drop database \"${POSTGRES_DB}\" at Docker process \"${DB_PROCESS_ID}\" (y/N)?" choice
case "${choice}" in
  y|Y ) echo "Continuing...";;
  n|N|* ) echo "Aborting!"; exit 0;;
esac

# Make sure to fix permissions on your mounted volume if docker is run as root
cp migrations/*.sql postgres-export || { echo "Cannot copy new migrations to docker mount point, quitting!"; exit 1; }

started=`date`
# Migrations run in reverse
for scriptpath in `ls -r migrations/*.down.sql`; do
  script=`basename "${scriptpath}"`
  echo "Running script: ${script} at `date`..."
  docker exec --user postgres -it ${DB_PROCESS_ID} psql -d ${POSTGRES_DB} -f "/export/${script}"
done
stopped=`date`
echo "Started at: ${started}"
echo "Stopped at: ${stopped}"
