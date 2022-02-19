#!/bin/bash

# Only try to kill if failed
if pid=$(pwdx "$(pgrep -f "java.*quarkus")" 2>/dev/null | grep Pensador | cut -d ":" -f 1); then
  kill "$pid" 2>/dev/null
fi
