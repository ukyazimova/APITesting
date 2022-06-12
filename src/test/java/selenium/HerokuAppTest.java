package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class HerokuAppTest {
    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(70));
        actions = new Actions(driver);
    }

    //Add/Remove Elements
    @Test
    public void addRemoveElements() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");
        WebElement addButton = driver.findElement(By.xpath("//div[@class='example']/button[@onclick='addElement()']"));
        Assert.assertTrue(addButton.isDisplayed());

        List<WebElement> elementContainer = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));

        Assert.assertTrue(elementContainer.isEmpty());
        Thread.sleep(1000);
    }

    // Basic Auth
    @Test
    public void basicAuth() throws InterruptedException {
        driver.get("https://admin:admin@the-internet.herokuapp.com/basic_auth");
        Thread.sleep(1500);
        WebElement text = driver.findElement(By.xpath("//div[@class='example']/p"));
        Assert.assertEquals(text.getText(), "Congratulations! You must have the proper credentials.");
    }

    //Challenging DOM

    // Checkboxes
    @Test
    public void checkboxes() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/checkboxes");
        WebElement checkBox1 = driver.findElement(By.xpath("//form[@id='checkboxes']//input[1]"));
        WebElement checkBox2 = driver.findElement(By.xpath("//form[@id='checkboxes']//input[2]"));

        boolean checkBoxState1 = checkBox1.isSelected();
        boolean checkBoxState2 = checkBox2.isSelected();

        if (checkBoxState1) {
            checkBox1.click();
            Assert.assertFalse(checkBox1.isSelected());
        } else {
            checkBox1.click();
            Assert.assertTrue(checkBox1.isSelected());
        }

        if (checkBoxState2) {
            checkBox2.click();
        }
        Thread.sleep(1500);
    }

    // Context Menu
    @Test
    public void contextMenu() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/context_menu");
        WebElement contextBox = driver.findElement(By.id("hot-spot"));
        actions.contextClick(contextBox).perform();
        Thread.sleep(1500);
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        Assert.assertEquals(alertText, "You selected a context menu");
        alert.dismiss();
    }

    // Disappearing Elements
    @Test
    public void disappearingElements() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/disappearing_elements");
       // WebElement homeButton = driver.findElement(By.xpath("//li/a[text()='Home']"));
       // WebElement aboutButton = driver.findElement(By.xpath("//li/a[text()='About']"));
       // WebElement contactButton = driver.findElement(By.xpath("//li/a[text()='Contact Us']"));
       // WebElement portfolioButton = driver.findElement(By.xpath("//li/a[text()='Portfolio']"));
        Thread.sleep(500);
        List<WebElement> elementContainer = driver.findElements(By.xpath("//div[@class='example']//li/descendant::*"));

        while(elementContainer.size()==4){
            driver.navigate().refresh();
            Thread.sleep(50);
            elementContainer = driver.findElements(By.xpath("//div[@class='example']//li/descendant::*"));
        }
        Assert.assertTrue(driver.findElement(By.xpath("//div[@class='example']//li[5]")).isDisplayed());
        Assert.assertEquals(driver.findElement(By.xpath("//div[@class='example']//li[5]")).getText(), "Gallery");
    }

    // Drag and Drop Dropdown
    @Test
    public void dragAndDrop() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/drag_and_drop");
        actions = new Actions(driver);
        WebElement elementA = driver.findElement(By.id("column-a"));
        WebElement elementB = driver.findElement(By.id("column-b"));
        Thread.sleep(5000);
        // actions.moveToElement(elementA).clickAndHold(elementA).moveToElement(elementB).release(elementB).build().perform();
        actions.dragAndDrop(elementA, elementB).build().perform();
        WebElement elementAHeader = driver.findElement(By.xpath("//div[@id='column-a']/header"));
        WebElement elementBHeader = driver.findElement(By.xpath("//div[@id='column-b']/header"));
        String aHeaderText = elementAHeader.getText();
        String bHeaderText = elementBHeader.getText();


        Thread.sleep(5000);
        Assert.assertEquals(aHeaderText, "B");
        Assert.assertEquals(bHeaderText, "A");

    }
    // Dynamic Content
    @Test
    public void dynamicContent() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/dynamic_content");
        WebElement text1 = driver.findElement(By.xpath("//div[@id='content']/div[1]/div[2]"));
        WebElement text2 = driver.findElement(By.xpath("//div[@id='content']/div[2]/div[2]"));
        WebElement text3 = driver.findElement(By.xpath("//div[@id='content']/div[3]/div[2]"));

        Thread.sleep(500);
        driver.navigate().refresh();

        Thread.sleep(500);

        Assert.assertNotEquals(text1, driver.findElement(By.xpath("//div[@id='content']/div[1]/div[2]")));
        Assert.assertNotEquals(text2, driver.findElement(By.xpath("//div[@id='content']/div[2]/div[2]")));
        Assert.assertNotEquals(text3, driver.findElement(By.xpath("//div[@id='content']/div[3]/div[2]")));
        WebElement clickHereLink = driver.findElement(By.xpath("//*[@id='content']/div//a[text()='click here']"));
        clickHereLink.click();
        Thread.sleep(500);
        driver.navigate().refresh();
        Thread.sleep(500);
        //Assert.assertEquals(text1 ,driver.findElement(By.xpath("//div[@id='content']/div[1]/div[2]")).getText());
        //Assert.assertEquals( driver.findElement(By.xpath("//div[@id='content']/div[2]/div[2]")).getText(),text2);
        Assert.assertNotEquals(text3, driver.findElement(By.xpath("//div[@id='content']/div[3]/div[2]")));

    }

    //  Dynamic Controls
    //  Dynamic Loading
    //  Floating Menu
    //  Hovers
    //  Multiple Windows
    //  Redirect Link

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
