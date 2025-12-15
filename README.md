# Selenium tests for WeMech

This folder contains Selenium-based automated tests (Chrome, headless) for the WeMech web app and helpers to run them locally or in CI.

## Test Implementations

### Java Tests (Recommended)
- Tests: `java-tests/src/test/java/com/wemech/WeMechSeleniumTests.java` (11 tests using JUnit 5)
- Build tool: Maven 3.9 with Java 17
- Dependencies: Selenium 4.38.0, WebDriverManager 5.4.0, JUnit Jupiter 5.10.0
- Container CI: `Dockerfile.tests-java` (installs Google Chrome and runs Maven tests)
- Jenkins: `Jenkinsfile` includes Java test stage

### Python Tests (Alternative)
- Tests: `test_app.py` (10 tests using pytest and headless Chrome)
- Test runner: `pytest` (JUnit XML produced at `test-results/results.xml`)
- Config: `pytest.ini`
- Container CI: `Dockerfile.tests` (installs Google Chrome and runs tests)
- Jenkins: `Jenkinsfile` includes Python test stage

Prerequisites (local)
- Python 3.8+
- Google Chrome installed (for local runs)
- PowerShell (Windows) or bash (Linux/macOS)
- Docker (for containerized runs)

Run locally (PowerShell)
```powershell
# Activate virtualenv
.\.venv\Scripts\Activate.ps1
# Install deps
pip install -r requirements.txt
# Set your app URL and run tests
$env:BASE_URL = 'http://<your-app-ip-or-host>:8081'
pytest -q --junitxml=test-results/results.xml
```

Run with the helper script (Windows PowerShell)
```powershell
# Optional: pass the base URL, default is http://16.171.224.162:8081
.\run_tests.ps1 -BaseUrl 'http://<your-app-ip-or-host>:8081'
```

Run Java tests in Docker (recommended for CI)
```powershell
# Build the Java test image (from Seleniumtests folder)
docker build -t wemech-tests-java -f Dockerfile.tests-java .
# Run tests (outputs to test-results/java/)
docker run --rm -e BASE_URL='http://16.171.224.162' -v ${PWD}/test-results/java:/app/test-results wemech-tests-java
```

Run Python tests in Docker
```powershell
# Build the Python test image
docker build -t wemech-tests -f Dockerfile.tests .
# Run tests
docker run --rm -e BASE_URL='http://16.171.224.162' -v ${PWD}\test-results:/app/test-results wemech-tests
```

Run Java tests locally (requires JDK 17+ and Maven)
```powershell
cd java-tests
mvn test -DbaseUrl=http://16.171.224.162
```

Jenkins notes
- The included `Jenkinsfile` builds and runs both Python and Java tests, publishing JUnit results.
- Ensure the Jenkins agent can run Docker and the container can reach `BASE_URL`.
- Java tests produce surefire reports in `test-results/java/`
- Python tests produce pytest XML in `test-results/results.xml`

Troubleshooting
- If `webdriver-manager` cannot download chromedriver in CI, pre-bake chromedriver into the image or allow outbound access.
- For headless failures, ensure Chrome is installed in the test environment and the container has required libraries.

Contact
- If you want, I can add a `GitHub Actions` workflow as well or pre-bake chromedriver into the Docker image.
