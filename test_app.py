import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# IMPORTANT: REPLACE with the public IP of your Assignment 2, Part I EC2 deployment!
# ASSUMPTION: Part I app is running on standard port 80.
BASE_URL = "http://13.51.242.233:8081" 

# Set up the Selenium WebDriver fixture
@pytest.fixture(scope="module")
def driver():
    # Configuration for Headless Chrome (REQUIRED for Jenkins on EC2)
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")
    
    # Initialize the WebDriver
    # NOTE: Chromedriver must be in the container's PATH (handled in the updated Dockerfile)
    driver = webdriver.Chrome(options=chrome_options)
    driver.implicitly_wait(10)
    yield driver
    driver.quit()

# ----------------- TEST CASES (Minimum 10 Required) -----------------

# 1. Verify Homepage Loads (Title Check)
def test_1_homepage_title(driver):
    driver.get(BASE_URL)
    assert "WeMech Car Service" in driver.title
    print("Test 1 Passed: Homepage title verified.")

# 2. Verify Database Connection Status (Assumes an element shows status)
def test_2_database_status_visible(driver):
    driver.get(BASE_URL)
    # Assumes an element with ID 'db_status' exists on the page
    db_element = driver.find_element(By.ID, "db_status") 
    assert "Connected" in db_element.text or "Listing" in db_element.text
    print("Test 2 Passed: DB connection status verified.")

# 3. Verify Navigation Link to Contact Page
def test_3_navigate_to_contact_page(driver):
    driver.get(BASE_URL)
    # Assumes a link with the text 'Contact' exists
    driver.find_element(By.LINK_TEXT, "Contact").click()
    WebDriverWait(driver, 5).until(EC.url_to_be(f"{BASE_URL}/contact"))
    assert driver.current_url == f"{BASE_URL}/contact"
    print("Test 3 Passed: Navigation to contact page verified.")

# 4. Verify successful form submission (Assuming /contact page)
def test_4_successful_form_submission(driver):
    driver.get(f"{BASE_URL}/contact") 
    driver.find_element(By.ID, "name").send_keys("Test User")
    driver.find_element(By.ID, "email").send_keys("test@example.com")
    driver.find_element(By.ID, "message").send_keys("Automated test message.")
    driver.find_element(By.ID, "submit_button").click()
    # Check for a success message after submission
    assert "Thank you" in driver.find_element(By.TAG_NAME, "body").text
    print("Test 4 Passed: Successful form submission verified.")

# 5. Verify input validation for empty fields (Form test)
def test_5_form_validation_empty_name(driver):
    driver.get(f"{BASE_URL}/contact")
    # Skip entering 'name'
    driver.find_element(By.ID, "email").send_keys("test@example.com")
    driver.find_element(By.ID, "submit_button").click()
    # Check for an error message or staying on the same page
    assert "Name is required" in driver.find_element(By.TAG_NAME, "body").text
    print("Test 5 Passed: Empty name validation verified.")

# 6. Verify correct header element text
def test_6_check_page_header(driver):
    driver.get(BASE_URL)
    # Assumes an h1 tag contains a specific welcome message
    header = driver.find_element(By.TAG_NAME, "h1")
    assert "Welcome" in header.text
    print("Test 6 Passed: Main page header text verified.")

# 7. Verify CSS class presence (Check for a primary button style)
def test_7_check_primary_button_style(driver):
    driver.get(BASE_URL)
    # Assumes a button exists with class 'btn-primary'
    button = driver.find_element(By.CLASS_NAME, "btn-primary")
    assert button is not None
    print("Test 7 Passed: Primary button style verified.")
    
# 8. Verify existence of an image logo/asset
def test_8_check_logo_image_exists(driver):
    driver.get(BASE_URL)
    # Assumes an image with ID 'app_logo' exists
    logo = driver.find_element(By.ID, "app_logo")
    assert logo.get_attribute("src") is not None
    print("Test 8 Passed: Logo image existence verified.")
    
# 9. Verify footer copyright date
def test_9_check_footer_copyright(driver):
    driver.get(BASE_URL)
    # Assumes a footer element exists with the copyright year
    footer = driver.find_element(By.TAG_NAME, "footer")
    assert "2025" in footer.text # Use a recent year
    print("Test 9 Passed: Footer copyright verified.")

# 10. Verify a specific element is initially hidden/invisible (e.g., a modal)
def test_10_modal_is_initially_hidden(driver):
    driver.get(BASE_URL)
    # Assumes a modal or alert box with ID 'success_alert'
    alert = driver.find_element(By.ID, "success_alert")
    # Use the is_displayed() method to check visibility
    assert not alert.is_displayed()
    print("Test 10 Passed: Success alert is initially hidden.")