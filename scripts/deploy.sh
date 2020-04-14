#!/bin/bash

GITCOMMANDS=""
if [[ "$1" == "vfb" ]]; then
    GITCOMMANDS="git fetch origin && git reset --hard origin/master"
else
    GITCOMMANDS="git fetch origin buggy_version && git reset --hard origin/buggy_version"
fi

ssh -A sen4ik@$2 -p$3 "cd /home/sen4ik/workspace/${1} &&
      sudo systemctl stop ${1} &&
      ${GITCOMMANDS} &&
      sed -i "/spring.devtools.restart.enabled=/c\spring.devtools.restart.enabled=false" src/main/resources/application.properties &&
      sed -i "/scheduler.enabled=/c\scheduler.enabled=true" src/main/resources/application.properties &&
      sed -i "/twilio.sid=/c\twilio.sid=${4}" src/main/resources/application.properties &&
      sed -i "/twilio.auth-token=/c\twilio.auth-token=${5}" src/main/resources/application.properties &&
      sed -i "/twilio.phone-number=/c\twilio.phone-number=${6}" src/main/resources/application.properties &&
      sed -i "/twilio.enabled=/c\twilio.enabled=true" src/main/resources/application.properties &&
      sed -i "/bible.api.key=/c\bible.api.key=${7}" src/main/resources/application.properties &&
      sed -i "/my.email=/c\my.email=${8}" src/main/resources/application.properties &&
      sed -i "/my.phone=/c\my.phone=${9}" src/main/resources/application.properties &&
      sed -i "/spring.mail.host=/c\spring.mail.host=${10}" src/main/resources/application.properties &&
      sed -i "/spring.mail.username=/c\spring.mail.username=${11}" src/main/resources/application.properties &&
      sed -i "/spring.mail.password=/c\spring.mail.password=${12}" src/main/resources/application.properties &&
      sed -i "/recaptcha.validation.key-site=/c\recaptcha.validation.key-site=${13}" src/main/resources/application.properties &&
      sed -i "/recaptcha.validation.secret-key=/c\recaptcha.validation.secret-key=${14}" src/main/resources/application.properties &&
      sed -i "/recaptcha.testing.enabled=/c\recaptcha.testing.enabled=false" src/main/resources/application.properties &&
      sed -i "/test.env.prefix=/c\test.env.prefix=" src/main/resources/application.properties &&
      mvn clean package -DskipTests=true &&
      cp target/vfb.war /opt/${1}/current/ &&
      sudo systemctl start ${1} &&
      sleep 10 &&
      tail -n 15 /opt/${1}/logs/spring.log"
