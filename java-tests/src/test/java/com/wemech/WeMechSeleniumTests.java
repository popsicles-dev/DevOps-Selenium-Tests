package com.wemech;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WeMechSeleniumTests {

    private WebDriver driver;
    private String baseUrl;

    @BeforeAll
    public void setUp() {
        // Determine base URL from system property or environment variable
        String fromProp = System.getProperty("baseUrl");
        String fromEnv = System.getenv("BASE_URL");
        baseUrl = (fromProp != null && !fromProp.isEmpty()) ? fromProp : (fromEnv != null ? fromEnv : "http://16.171.224.162");

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void testHomepageTitle() {
        driver.get(baseUrl);
        Assertions.assertTrue(driver.getTitle().contains("WeMech"), "Title should contain brand 'WeMech'");
    }

    @Test
    @Order(2)
    public void testPageContainsCatalogueSection() {
        driver.get(baseUrl);
        String body = driver.findElement(By.tagName("body")).getText();
        Assertions.assertTrue(body.contains("CATALOGUE") || body.contains("U R B A N"));
    }

    @Test
    @Order(3)
    public void testNavigationBarExists() {
        driver.get(baseUrl);
        WebElement nav = driver.findElement(By.className("topnavbar"));
        Assertions.assertNotNull(nav, "Top navigation bar should exist");
    }

    @Test
    @Order(4)
    public void testHomeNavigationLinkWorks() {
        driver.get(baseUrl);
        WebElement homeLink = driver.findElement(By.linkText("HOME"));
        homeLink.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/home"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/home"));
    }

    @Test
    @Order(5)
    public void testLoginPageLoads() {
        driver.get(baseUrl + "/login");
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
        // Verify login form exists with correct field names
        WebElement emailField = driver.findElement(By.name("mail"));
        WebElement passwordField = driver.findElement(By.name("pass"));
        Assertions.assertNotNull(emailField);
        Assertions.assertNotNull(passwordField);
    }
    
    @Test
    @Order(6)
    public void testSuccessfulLogin() {
        driver.get(baseUrl + "/login");
        driver.findElement(By.name("mail")).sendKeys("hifsashafique8@gmail.com");
        driver.findElement(By.name("pass")).sendKeys("hifsa");
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        // Use JavaScript click for more reliability
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", submitButton);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Wait for redirect - URL should change to loginverify or another page
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(baseUrl + "/login")));
        // Verify URL changed (loginverify, home, or dashboard)
        Assertions.assertNotEquals(baseUrl + "/login", driver.getCurrentUrl());
    }

    @Test
    @Order(7)
    public void testPageHeaderContainsExpectedText() {
        driver.get(baseUrl);
        java.util.List<WebElement> headers = driver.findElements(By.tagName("h1"));
        boolean foundExpected = false;
        for (WebElement header : headers) {
            String headerText = header.getText();
            if (headerText.contains("U R B A N") || headerText.contains("CATALOGUE") || headerText.contains("E S C A P E")) {
                foundExpected = true;
                break;
            }
        }
        Assertions.assertTrue(foundExpected, "Should find header with URBAN, CATALOGUE, or ESCAPE text");
    }

    @Test
    @Order(8)
    public void testSearchFormExists() {
        driver.get(baseUrl);
        WebElement searchForm = driver.findElement(By.className("searchform"));
        Assertions.assertNotNull(searchForm, "Search form should exist");
    }

    @Test
    @Order(9)
    public void testLogoImageExists() {
        driver.get(baseUrl);
        WebElement logo = driver.findElement(By.className("logo"));
        WebElement img = logo.findElement(By.tagName("img"));
        String src = img.getAttribute("src");
        Assertions.assertNotNull(src);
        Assertions.assertTrue(src.contains("logo"));
    }

    @Test
    @Order(10)
    public void testFooterCopyright() {
        driver.get(baseUrl);
        WebElement footer = driver.findElement(By.className("copyright"));
        String footerText = footer.getText();
        Assertions.assertTrue(footerText.contains("2024") || footerText.contains("Copy right"));
    }

    @Test
    @Order(11)
    public void testSocialMediaLinksExist() {
        driver.get(baseUrl);
        WebElement socialIcon = driver.findElement(By.className("socialicon"));
        Assertions.assertNotNull(socialIcon);
        java.util.List<WebElement> links = socialIcon.findElements(By.tagName("a"));
        Assertions.assertTrue(links.size() >= 3, "Should have at least 3 social media links");
    }
}
