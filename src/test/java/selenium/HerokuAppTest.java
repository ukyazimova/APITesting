package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class HerokuAppTest {
    WebDriver driver;
    WebDriverWait wait;
    Actions actions;
    JavascriptExecutor js;


    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(70));
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    //Add/Remove Elements
    @Test
    public void addRemoveElements() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");
        WebElement addButton = driver.findElement(By.xpath("//div[@class='example']/button[@onclick='addElement()']"));
        Assert.assertTrue(addButton.isDisplayed());

        List<WebElement> elementContainer = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));

        Assert.assertTrue(elementContainer.isEmpty());
        Thread.sleep(50);
        for (int i = 0; i < 3; i++) {
            addButton.click();

        }
        elementContainer = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));
        Assert.assertEquals(elementContainer.size(), 3);
        Thread.sleep(20);
        WebElement deleteButton = driver.findElement(By.xpath("//*[@id='elements']/button[@onclick='deleteElement()']"));
        deleteButton.click();
        elementContainer = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));
        Assert.assertEquals(elementContainer.size(), 2);

        for (int i = 0; i < 2; i++) {
            deleteButton.click();
            Thread.sleep(10);
        }
        elementContainer = driver.findElements(By.xpath("//div[@id='elements']/descendant::*"));
        Assert.assertTrue(elementContainer.isEmpty());

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

        while (elementContainer.size() == 4) {
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

        //Create JS Executor
        //JavascriptExecutor js = (JavascriptExecutor) driver;
        // js.executeScript();
    }

    //  Dynamic Controls
    @Test
    public void dynamicControls() {
        driver.get("https://the-internet.herokuapp.com/dynamic_controls");
        WebElement removeButton = driver.findElement(By.xpath("//*[@class='example']//button[text()='Remove']"));
        WebElement checkbox = driver.findElement(By.id("checkbox"));
        Assert.assertTrue(checkbox.isDisplayed());
        //click remove
        removeButton.click();
        //wait animation
        //  WebElement loadAnimation = driver.findElement(By.xpath("//div[@id='loading']"));
        // wait.until(ExpectedConditions.invisibilityOf(loadAnimation));
        // Assert.assertFalse(checkbox.isDisplayed());
        // Assert.assertEquals(driver.findElement(By.id("message")).getText(), "It's gone!");

        //fluent wait
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);

        WebElement loadingBar = wait.until(new Function<WebDriver, WebElement>() {

            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.xpath("//div[@id='loading']"));

            }
        });
        wait.until(ExpectedConditions.invisibilityOf(checkbox));
        Assert.assertEquals(driver.findElement(By.id("message")).getText(), "It's gone!");

    }

    //  Dynamic Loading
    @Test
    public void dynamicLoading() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/dynamic_loading");
        By startButton = By.xpath("//div[@id='start']/button");
        By helloText = By.xpath("//div[@id='finish']");

        WebElement startButtonElement = driver.findElement(startButton);
        startButtonElement.click();
        WebElement helloWorldTextElement = driver.findElement(helloText);

        Thread.sleep(30);

    }

    //  Floating Menu
    @Test
    public void floatingMenu() throws InterruptedException {
        driver.get("https://the-internet.herokuapp.com/floating_menu");
        WebElement homeButton = driver.findElement(By.xpath("//*[@id='menu']//a[@href='#home']"));
        Assert.assertTrue(homeButton.isDisplayed());
        //scroll page down
        js.executeScript("window.scrollBy(0,2000)");
        Assert.assertTrue(homeButton.isDisplayed());
        //scroll up
        js.executeScript("window.scrollBy(0,-1000)");
        Assert.assertTrue(homeButton.isDisplayed());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='menu']//a[@href='#home']")));
        js.executeScript("arguments[0].click();", homeButton);
        Assert.assertTrue(homeButton.isDisplayed());
        //Thread.sleep(50);
    }
    //  Hovers
    //  Multiple Windows
    //  Redirect Link

    @Test
    public void switchWindow() {

    }

    @Test
    public void iFrames() {
        driver.get("https://the-internet.herokuapp.com/iframe");

        driver.switchTo().frame("mce_0_ifr");
        WebElement textElement = driver.findElement(By.xpath("*[@id='tinymce']//p"));
        textElement.clear();
        textElement.sendKeys("random text");
        driver.switchTo().defaultContent();
        WebElement headerTxt = driver.findElement(By.xpath("*[@class='example']//h3"));


    }

    @Test
    public void nestedFrames() {
        driver.get("https://the-internet.herokuapp.com/nested_frames");

        driver.switchTo().frame("frame-top").switchTo().frame("frame-left");
        WebElement leftFrameBody = driver.findElement(By.xpath("//body"));
        Assert.assertEquals(leftFrameBody.getText(), "LEFT");

        //driver.switchTo().parentFrame();
        driver.switchTo().defaultContent();
        driver.switchTo().frame("frame-middle");
        WebElement middleFrameBody = driver.findElement(By.xpath("//body"));
        Assert.assertEquals(middleFrameBody.getText(), "MIDDLE");

    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
