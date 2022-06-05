package Selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InitialSeleniumTest {


    public static void main(String[] args) {
        //1st approach - setting chrome driver manually
        //System.setProperty("webdriver.chrome.driver", "drivers/chromedriver103.exe");

        //2nd approach - use WebDriverManager library
        WebDriverManager.chromedriver().setup();


        ChromeDriver driver = new ChromeDriver();
        driver.get("http://training.skillo-bg.com");
        driver.manage().window().maximize();
        WebElement loginButton = driver.findElement(By.id("nav-link-login"));

        WebElement homeButton = driver.findElement(By.linkText("Home"));

        loginButton.click();

        //add wait in here
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

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
        Assert.assertEquals("ukyazimova", usernameProfile);
        driver.close();
    }


}
