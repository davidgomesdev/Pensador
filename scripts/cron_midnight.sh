#!/bin/bash

## At midnight, the process is running so we must kill it first
bash scripts/retry_check_network.sh && mkdir -p logs && bash scripts/kill.sh && bash scripts/update.sh > "logs/latest_midnight.cron" 2>&1 || exit 1

bash scripts/run.sh
