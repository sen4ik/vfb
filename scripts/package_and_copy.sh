#!/bin/bash
SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
mvn clean package -DskipTests=true && \
sudo cp $SCRIPTPATH/../target/vfb.war /opt/tomcat/webapps && \
sudo systemctl restart tomcat
