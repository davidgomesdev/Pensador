#!/bin/bash

TEMP_PACKAGE_PATH="/tmp/pensador_quarkusPackage.zip"

echo "# Downloading the latest"
echo
## We purposefully exit with success, because the update is a best effort
wget -O "$TEMP_PACKAGE_PATH" https://github.com/LegendL3n/Pensador/releases/download/latest/quarkusPackage.zip || exit 0

echo
echo "# Deleting current version"
rm -rf quarkus-app

echo
echo "# Unzipping"
echo
unzip "$TEMP_PACKAGE_PATH" -d quarkus-app || exit 1
rm -rf "$TEMP_PACKAGE_PATH"

echo
echo "* Update complete *"
