#!/bin/bash
# Create and load database tables.
# Executes against a running docker postgres container.
# Make sure to generate loadable data files first by running ItemParser and ReviewParser.
DB_PROCESS_ID=`docker ps | grep postgres | awk '{print $1}' | head -n 1`

read -p "Really create and load database \"${POSTGRES_DB}\" at Docker process \"${DB_PROCESS_ID}\" (y/N)?" choice
case "${choice}" in
  y|Y ) echo "Continuing...";;
  n|N|* ) echo "Aborting!"; exit 0;;
esac

read -p "Wipe and replace data (y/N)?" wipe

# Make sure to fix permissions on your mounted volume if docker is run as root
cp migrations/*.sql data/export || { echo "Cannot copy new migrations to docker mount point, quitting!"; exit 1; }
if [ "${wipe}" ]; then
  GROUP=`id -gn`
  chown "${USER}" data/import/*.csv && chgrp "${GROUP}" data/import/*.csv || echo "Warning: Cannot fix data file permissions"
  rsync -a data/import/*.csv data/export
fi

started=`date`
for scriptpath in migrations/*.up.sql; do
  script=`basename "${scriptpath}"`
  echo "Running script: ${script} at `date`..."
  docker exec --user postgres -it ${DB_PROCESS_ID} psql -d ${POSTGRES_DB} -f "/export/${script}"
done
stopped=`date`
echo "Started at: ${started}"
echo "Stopped at: ${stopped}"
