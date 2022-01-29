#!/bin/bash

kill "$(pwdx "$(pgrep -f "java.*quarkus")" 2>/dev/null | grep Pensador | cut -d ":" -f 1)" 2>/dev/null || exit 0
