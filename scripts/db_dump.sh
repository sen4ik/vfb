#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
PROPERTIES_FILE="${SCRIPTPATH}/../src/main/resources/application.properties"

if [ ! -f $PROPERTIES_FILE ]; then
    echo "Can't find "$PROPERTIES_FILE
    exit 1
fi

HOST=$(awk '/spring.datasource.url/{print $NF}' $PROPERTIES_FILE | awk -F':' '{print $3}' | awk -F'/' '{print $3}')
PORT=$(awk '/spring.datasource.url/{print $NF}' $PROPERTIES_FILE | awk -F':' '{print $4}' | awk -F'/' '{print $1}')
DBNAME=$(awk '/spring.datasource.url/{print $NF}' $PROPERTIES_FILE | awk -F'/' '{print $4}')
USER=$(awk '/spring.datasource.username/{print $NF}' $PROPERTIES_FILE | awk -F'=' '{print $2}')
PASSWORD=$(awk '/spring.datasource.password/{print $NF}' $PROPERTIES_FILE | awk -F'=' '{print $2}')
DATEANDTIME=`date +%m-%d-%Y_%H-%M-%S`
DB_BACKUP_FILE="${SCRIPTPATH}/../db_dump/$DBNAME-$DATEANDTIME.sql.gz"

mysqldump --column-statistics=0 -h$HOST -u$USER -p$PASSWORD -P$PORT $DBNAME | gzip > $DB_BACKUP_FILE
