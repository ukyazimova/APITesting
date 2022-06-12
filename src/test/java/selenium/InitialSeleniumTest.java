package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;

public class InitialSeleniumTest {
    ChromeDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

/*
        //Headless implementation
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920, 1200", "--ignore-certificate-errors");
        driver=new ChromeDriver(options);
*/
        //Implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(70));
        //Explicit wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(70));
        //driver settings
        driver.manage().window().maximize();
    }

    @Test
    public void login() {
        //1st approach - setting chrome driver manually
        //System.setProperty("webdriver.chrome.driver", "drivers/chromedriver103.exe");

        //2nd approach - use WebDriverManager library

        driver.get("http://training.skillo-bg.com");

        WebElement loginButton = driver.findElement(By.id("nav-link-login"));

        WebElement homeButton = driver.findElement(By.linkText("Home"));

        loginButton.click();

        //add wait in here

        WebElement usernameField = driver.findElement(By.id("defaultLoginFormUsername"));
        WebElement passwordField = driver.findElement(By.id("defaultLoginFormPassword"));
        usernameField.click();
        usernameField.clear();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        usernameField.sendKeys("ukyazimova");
        passwordField.click();
        passwordField.clear();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        passwordField.sendKeys("Password1");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        WebElement sigInButton = driver.findElement(By.id("sign-in-button"));
        sigInButton.click();
        WebElement newPostButton = driver.findElement(By.id("nav-link-new-post"));
        Assert.assertTrue(newPostButton.isDisplayed());
        homeButton.click();

        WebElement profileButton = driver.findElement(By.xpath("//a[contains(text(),'Profile')]"));
        profileButton.click();
        WebElement usernameProfile = driver.findElement(By.xpath("//div[contains(@class,'profile-user')]//h2"));
        Assert.assertTrue(usernameProfile.isDisplayed());
        //Assert.assertEquals("ukyazimova", usernameProfile);
        // driver.close();
    }



    @Test
    public void dropDownTest() {
        driver.get("https://www.mobile.bg/pcgi/mobile.cgi");
        WebElement cookiesConsentButton = driver.findElement(By.xpath("//div[@class='fc-footer-buttons-container']//button[@class='fc-button fc-cta-consent fc-primary-button']//p[@class='fc-button-label']"));
        cookiesConsentButton.click();
        Select dropDownMarka = new Select(driver.findElement(By.xpath("//select[@name='marka']")));
        dropDownMarka.selectByVisibleText("Lada");

        Select dropDownModel = new Select(driver.findElement(By.xpath("//select[@name='model']")));
        dropDownModel.selectByVisibleText("Niva");

        WebElement searchButton = driver.findElement(By.xpath("//*[@id='button2']"));
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();

        WebElement resultContainer = driver.findElement(By.xpath("//table[@class='tablereset'][2]"));
        Assert.assertTrue(resultContainer.isDisplayed());


    }
    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
