# User Guide: CI/CD Command Line Tool (`cicd`)

**Version:** `1.0`  
**Last Updated:** `March 2025`  
**Author:** Yiwen

---

## 📖 Table of Contents

- [🚀 Installation](#-installation)
- [🛠️ Basic Usage](#-basic-usage)
- [📋 Command Reference](#-command-reference)
    - [`check`](#check-validate-pipeline)
    - [`run`](#run-execute-a-pipeline)
    - [`dry-run`](#dry-run-simulate-execution)
    - [`report`](#report-fetch-execution-results)
- [🧹 Uninstallation](#-uninstallation)
- [📌 Notes](#-notes)
- [💡 Alternative Without Installation](#-alternative-without-installation)

---

## 🚀 Installation

### **1️⃣ Run the Install Script**

To set up the `cicd` command globally, run:

```sh
bash scripts/install.sh
```

**This will:**
✅ Create the `cicd` command in `/usr/local/bin`  
✅ Ensure `cicd` always runs the latest JAR version  
✅ Allow you to execute commands without `java -jar ...`

### **2️⃣ Verify Installation**

Run:

```sh
cicd --help
```

If the help message appears, installation is successful 🎉

---

## 🛠️ Basic Usage

Once installed, use `cicd` like this:

```sh
cicd <command> [options]
```

Example:

```sh
cicd check -f .pipelines/pipeline.yaml
```

Runs the pipeline configuration check.

---

## 📋 Command Reference

### 🔹 `check` (Validate Pipeline)

Check if the pipeline configuration is valid:

```sh
cicd check -f .pipelines/pipeline.yaml
```

### 🔹 `run` (Execute a Pipeline)

#### **Run Locally**

```sh
cicd run --local --repo /home/user/project --commit abc123 --branch main --pipeline pr
```

**Variations:**

- If `--branch` is missing → uses `main`
- If `--commit` is missing → uses latest commit
- If `--pipeline` is missing → requires `--file`

#### **Run Remotely**

```sh
cicd run --repo https://github.com/org/repo.git --branch main --pipeline pr
```

- **Sample Repo**: https://github.com/YiwenW312/cicdSample.git
### 🔹 `dry-run` (Simulate Execution)

Show execution order **without actually running jobs**:

```sh
cicd dry-run -f .pipelines/pipeline.yaml
```

**Example Output:**

```yaml
build:
  compile:
    image: gradle:8.12-jdk21
    script:
      - ./gradlew classes
test:
  unittests:
    image: gradle:8.12-jdk21
    script:
      - ./gradlew test
  reports:
    image: gradle:8.12-jdk21
    script:
      - ./gradlew check
```

### 🔹 `report` (Fetch Execution Results)

To retrieve execution results:

```sh
cicd report --pipeline my_pipeline --format json
```

**Formats:**

- `plaintext` (default)
- `json`
- `yaml`

---

## 🧹 Uninstallation

To remove the `cicd` command:

```sh
sudo rm /usr/local/bin/cicd
```

---

## 📌 Notes

- All commands require a **valid `.yaml` pipeline file**.
- If running locally, results are stored in a **local report file**.
- If running remotely, results are saved in the **database**.

---

## 💡 Alternative Without Installation

If you do **not** install the script, you need to run commands manually with `java -jar`:

```sh
java -jar cli/build/libs/ci-tool.jar check -f .pipelines/pipeline.yaml
```

Instead of:

```sh
cicd check -f .pipelines/pipeline.yaml
```

To run a pipeline:

```sh
java -jar cli/build/libs/ci-tool.jar run --local --repo /home/user/project --commit abc123 --branch main --pipeline pr
```

This method works, but **installing the script makes it easier** 🚀