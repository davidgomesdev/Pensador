#!/bin/bash

pid="$(pwdx "$(pgrep -f "java.*quarkus")" 2>/dev/null | grep Pensador | cut -d ":" -f 1)"

if [[ -n "$pid" ]]; then
  echo "Process is running, killing it..."
  kill "$pid" 2>/dev/null
  echo "Process killed"
else
  echo "Process not running"
fi
