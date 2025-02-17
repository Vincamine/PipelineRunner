# Generate Jar
./gradlew clean build
./gradlew clean shadowJar
#

# Check Installation
java -jar app/build/libs/ci-tool.jar --help

# Check pipeline configuration
echo "Check pipeline configuration"
java -jar app/build/libs/ci-tool.jar check -f .pipelines/00-valid-file.yaml

#java -jar app/build/libs/ci-tool.jar logs --id 12345


### ERROR HANDLING
# Check file Path
echo "run check without argument"
java -jar app/build/libs/ci-tool.jar check
echo "Non-exist file"
java -jar app/build/libs/ci-tool.jar check -f .pipelines/non-exist-file.yaml
echo "File outside ./pipeline"
java -jar app/build/libs/ci-tool.jar check -f 03-outside-pipeline.yaml



# Check fields
echo "Check field"
java -jar app/build/libs/ci-tool.jar check -f .pipelines/01-miss-name.yaml
java -jar app/build/libs/ci-tool.jar check -f .pipelines/02-wrong-name-type.yaml

#Detect Circular Dependencies
echo "Check dependencies"
java -jar app/build/libs/ci-tool.jar check -f .pipelines/04-cycle-direct.yaml
java -jar app/build/libs/ci-tool.jar check -f .pipelines/05-cycle-self.yaml
java -jar app/build/libs/ci-tool.jar check -f .pipelines/06-cycle-complex.yaml
java -jar app/build/libs/ci-tool.jar check -f .pipelines/07-multi-path-dependencies.yaml

 Run Command
java -jar app/build/libs/ci-tool.jar dry-run

# QUESTION: Do we have -f?
java -jar app/build/libs/ci-tool.jar run -f .pipelines/00-valid-file.yaml