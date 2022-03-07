#!/bin/bash

bash scripts/retry_check_network.sh || exit 1

mkdir -p logs && bash scripts/update.sh >"logs/latest_$1.cron" 2>&1 || exit 1
