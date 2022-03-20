#!/usr/bin/env

TEMP_PACKAGE_PATH="/tmp/pensador_quarkusPackage.zip"
MODE="$1"

echo "# Downloading the latest"
echo
## We purposefully exit with success, because the update is a best effort
wget -q -O "$TEMP_PACKAGE_PATH" https://github.com/LegendL3n/Pensador/releases/download/latest/quarkusPackage.zip || exit 0

echo "# Checking if newer"

function update() {
  echo
  echo "# Stopping the process..."

  bash scripts/kill.sh || exit 1

  echo
  echo "# Deleting current version"
  rm -rf quarkus-app

  echo
  echo "# Unzipping"
  echo
  unzip -q "$TEMP_PACKAGE_PATH" -d quarkus-app || exit 1
  md5sum "$TEMP_PACKAGE_PATH" >current_package.md5
  rm -rf "$TEMP_PACKAGE_PATH"
}

echo
if [ -f current_package.md5 ]; then
  if md5sum -c current_package.md5 >/dev/null 2>&1; then
    echo "App already updated"
    echo

    # Only in reboot is the run needed when updated
    if [ "$MODE" != "reboot" ]; then
      exit 0
    fi
  else
    echo "Outdated! Updating..."
    update
  fi
else
  echo "There's no hash of current, updating..."
  update
fi

echo "* Update complete *"

bash scripts/run.sh
