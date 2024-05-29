# Migrations

This folder contains PostgreSQL SQL scripts to export and drop, and then create and load the project's database contents.

The following scripts run all the *.up.sql or *.down.sql files against the docker database instance:

```bash
# Create database tables and load data in data/export
./docker-db-up.sh

# Export data to data/export and drop database tables
./docker-db-down.sh
```

## Notes

The export+drop and create+load steps have been combined here for convenience, and no verification is done.

Foreign key constraints are used, along with bigint primary keys (instead of uuid/varchar). This could be changed to support sharding or to ease complex migrations.

If you prefer, you may use the migrate tool instead of the shell scripts: https://github.com/golang-migrate/migrate

In [docker-db-up.sh](/docker-db-up.sh), *.test.sql versions of files replace their *.sql counterparts for testing setup only.
