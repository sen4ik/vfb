#!/bin/bash
# get database from application.properties
ORIGINAL_DB_NAME=$(awk '/spring.datasource.url/{print $NF}' ${15} | awk -F'/' '{print $4}' | awk -F'?' '{print $1}')
GIT_COMMAND=""
TEST_PREFIX=""
# if it is production database, leave its name as it is
UPDATED_DB_NAME=$ORIGINAL_DB_NAME
SCHEDULER_ENABLED="true"
if [[ "$1" == "vfb" ]]; then
    GIT_COMMAND="git fetch origin && git reset --hard origin/master"
elif [[ "$1" == "vfb_buggy" ]]; then
    # reset buggy_version branch to head
    GIT_COMMAND="git fetch origin buggy_version && git reset --hard origin/buggy_version"
    TEST_PREFIX="dev:"
    # if it is vfb_buggy we need to update db name
    UPDATED_DB_NAME=$1
    SCHEDULER_ENABLED="false"
else
    echo "Unexpected parameter: ${1}"
    exit 1;
fi

ssh -A sen4ik@$2 -p$3 "cd /home/sen4ik/workspace/${1} &&
      sudo systemctl stop ${1} &&
      ${GIT_COMMAND} &&
      sed -i "/spring.devtools.restart.enabled=/c\spring.devtools.restart.enabled=false" ${15} &&
      sed -i "/scheduler.enabled=/c\scheduler.enabled=${SCHEDULER_ENABLED}" ${15} &&
      sed -i "/twilio.sid=/c\twilio.sid=${4}" ${15} &&
      sed -i "/twilio.auth-token=/c\twilio.auth-token=${5}" ${15} &&
      sed -i "/twilio.phone-number=/c\twilio.phone-number=${6}" ${15} &&
      sed -i "/twilio.enabled=/c\twilio.enabled=true" ${15} &&
      sed -i "/bible.api.key=/c\bible.api.key=${7}" ${15} &&
      sed -i "/my.email=/c\my.email=${8}" ${15} &&
      sed -i "/my.phone=/c\my.phone=${9}" ${15} &&
      sed -i "/spring.mail.host=/c\spring.mail.host=${10}" ${15} &&
      sed -i "/spring.mail.username=/c\spring.mail.username=${11}" ${15} &&
      sed -i "/spring.mail.password=/c\spring.mail.password=${12}" ${15} &&
      sed -i "/recaptcha.validation.key-site=/c\recaptcha.validation.key-site=${13}" ${15} &&
      sed -i "/recaptcha.validation.secret-key=/c\recaptcha.validation.secret-key=${14}" ${15} &&
      sed -i "/recaptcha.testing.enabled=/c\recaptcha.testing.enabled=false" ${15} &&
      sed -i "/test.env.prefix=/c\test.env.prefix=${TEST_PREFIX}" ${15} &&
      sed -i "s/${ORIGINAL_DB_NAME}/${UPDATED_DB_NAME}/g" ${15} &&
      mvn clean package -DskipTests=true &&
      cp target/vfb.war /opt/${1}/current/ &&
      sudo systemctl start ${1} &&
      sleep 10 &&
      echo '' &&
      tail -n 15 /opt/${1}/logs/spring.log"
