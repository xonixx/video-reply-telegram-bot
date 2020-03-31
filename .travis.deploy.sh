#!/usr/bin/env bash

set -x

USER=apps1
SERV=prod.cmlteam.com
APP=ds-infra

scp -i /tmp/deploy_rsa $APP.conf target/$APP.jar $USER@$SERV:~/
ssh -i /tmp/deploy_rsa $USER@$SERV "
if [ ! -f /etc/init.d/$APP ]
then
    sudo ln -s /home/$USER/$APP.jar /etc/init.d/$APP
fi
sudo /etc/init.d/$APP restart
"