#!/usr/bin/env bash

APP=video_reply_telegram_bot
USER=apps1
SERV=prod.cmlteam.com

if [[ -z "$TELEGRAM_BOT_ADMIN_USER" || -z "$TELEGRAM_BOT_TOKEN" ]] ; then
    echo "You must set TELEGRAM_BOT_ADMIN_USER and TELEGRAM_BOT_TOKEN env. vars" >&2
    exit 1
fi

echo
echo "BUILD..."
echo

./mvnw clean package -DskipTests

echo
echo "DEPLOY..."
echo

scp $APP.conf target/$APP.jar $USER@$SERV:~/

echo
echo "RESTART..."
echo

ssh $USER@$SERV "
if [ ! -f /etc/init.d/$APP ]
then
    sudo ln -s /home/$USER/$APP.jar /etc/init.d/$APP
    sudo update-rc.d $APP defaults 99
fi
sudo \
    TELEGRAM_BOT_ADMIN_USER=$TELEGRAM_BOT_ADMIN_USER \
    TELEGRAM_BOT_TOKEN=$TELEGRAM_BOT_TOKEN \
    /etc/init.d/$APP restart
sleep 20
tail -n 200 /var/log/$APP.log
"