#!/usr/bin/env bash

echo "
create database video_reply_telegram_bot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
grant all privileges on video_reply_telegram_bot.* to 'video_reply_telegram_bot'@'%' identified by 'video_reply_telegram_bot';
flush privileges;
" | mysql -uroot -p
