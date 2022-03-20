#!/usr/bin/env

bash scripts/retry_check_network.sh || exit 1

mkdir -p logs && bash scripts/update.sh "$1" >"logs/latest_$1.cron" 2>&1 || exit 1
