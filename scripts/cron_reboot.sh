#!/bin/bash

bash scripts/retry_check_network.sh && mkdir -p logs && bash scripts/update.sh > logs/latest_reboot.cron 2>&1 || exit 1

bash scripts/run.sh
