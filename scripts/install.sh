#!/bin/bash

# Define the path to the CLI JAR
CLI_JAR_PATH="$(pwd)/cli/build/libs/ci-tool.jar"

# Check if the JAR file exists
if [ ! -f "$CLI_JAR_PATH" ]; then
    echo "[ERROR] ci-tool.jar not found at $CLI_JAR_PATH"
    echo "==>> Make sure you have built the CLI with: ./gradlew clean build <<=="
    exit 1
fi

# Create the wrapper script in /usr/local/bin
echo '#!/bin/bash' | sudo tee /usr/local/bin/cicd > /dev/null
echo "java -jar $CLI_JAR_PATH \"\$@\"" | sudo tee -a /usr/local/bin/cicd > /dev/null
sudo chmod +x /usr/local/bin/cicd

echo "[SUCCESS] Installation complete! You can now use 'cicd' as a command."
echo "==>> Try running: cicd --help <<=="