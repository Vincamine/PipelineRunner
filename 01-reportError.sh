echo "Building the CI/CD tool..."
./gradlew clean build
./gradlew clean shadowJar

echo "----- Testing Report Command -----"

# Test help
echo -e "\n>>> Testing help command"
java -jar app/build/libs/ci-tool.jar report --help

# Test pipeline listing
echo -e "\n>>> Testing pipeline listing"
java -jar app/build/libs/ci-tool.jar report --local

# Test pipeline1 runs
echo -e "\n>>> Testing pipeline1 runs"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline1

# Test pipeline1 run1
echo -e "\n>>> Testing pipeline1 run1"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline1 --run 1

# Test pipeline1 run2 (failed run)
echo -e "\n>>> Testing pipeline1 run2 (failed run)"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline1 --run 2

# Test build stage
echo -e "\n>>> Testing build stage"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline1 --run 1 --stage build

# Test compile job
echo -e "\n>>> Testing compile job"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline1 --run 1 --stage build --job compile

# Test pipeline2 deploy
echo -e "\n>>> Testing pipeline2 deploy"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline2 --run 1 --stage deploy

# Test db-migration job
echo -e "\n>>> Testing db-migration job"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline2 --run 1 --stage deploy --job db-migration

# Test error cases
echo -e "\n>>> Testing missing repository"
java -jar app/build/libs/ci-tool.jar report --pipeline pipeline1

echo -e "\n>>> Testing non-existent pipeline"
java -jar app/build/libs/ci-tool.jar report --local --pipeline nonexistent

echo -e "\n>>> Testing non-existent stage"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline1 --run 1 --stage nonexistent

echo -e "\n>>> Testing job without stage"
java -jar app/build/libs/ci-tool.jar report --local --pipeline pipeline1 --run 1 --job compile