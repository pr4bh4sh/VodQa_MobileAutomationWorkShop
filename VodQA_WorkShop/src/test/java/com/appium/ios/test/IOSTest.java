package com.appium.ios.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * <a href="https://github.com/appium/appium">Appium</a> test which runs against a local Appium instance deployed
 * with the 'UICatalog' iPhone project which is included in the Appium source distribution.
 *
 * @author Ross Rowe
 */
@SuppressWarnings("deprecation")
public class IOSTest {

  private AppiumDriver<IOSElement> driver;

  private WebElement row;
  int height, width;

  @Before
  public void setUp() throws Exception {
    // set up appium
    File classpathRoot = new File(System.getProperty("user.dir"));
    File appDir = new File(classpathRoot, "/build/");
    File app = new File(appDir, "UICatalog.app");
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("platformVersion", "11.0");
    capabilities.setCapability("deviceName", "iPhone 7");
    capabilities.setCapability("app", app.getAbsolutePath());
    capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
    capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
    driver = new IOSDriver<IOSElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    height = driver.manage().window().getSize().height;
    width = driver.manage().window().getSize().width;
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }

  private void openMenuPosition(int index) {
    //populate text fields with two random number
    MobileElement table = (MobileElement) driver.findElementByClassName("XCUIElementTypeTable");
    row = table.findElementsByClassName("XCUIElementTypeCell").get(index);
    row.click();
  }

  private Point getCenter(WebElement element) {

    Point upperLeft = element.getLocation();
    Dimension dimensions = element.getSize();
    return new Point(upperLeft.getX() + dimensions.getWidth() / 2,
        upperLeft.getY() + dimensions.getHeight() / 2);
  }

  public void scrollWithTouchAction(String direction, int scrollCount) {
    TouchAction touchAction;
    touchAction = new TouchAction(driver);
    int startX = width / 2;
    int startY = height / 2 - 100;
    int endX = 0;
    int endY = height - 200;

    for (int i = 0; i < scrollCount; i++) {
      if (direction.equals("down")) {
        touchAction.press(startX, startY).moveTo(0, -endY).perform();
      } else if (direction.equals("up")) {
        touchAction.press(endX, endY).moveTo(startX, startY).perform();
      }
    }
  }

  public void scrollWithJS(String dirction, int times) {
    try {
      JavascriptExecutor js = driver;
      HashMap<String, String> param = new HashMap<>();
      param.put("direction", dirction);
      for (int i = 0; i < times; i++) {
        js.executeScript("mobile: scroll", param);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testFindElement() throws Exception {
    //first view in UICatalog is a table
    IOSElement table = driver.findElementByClassName("XCUIElementTypeTable");
    assertNotNull(table);
    //is number of cells/rows inside table correct
    List<MobileElement> rows = table.findElementsByClassName("XCUIElementTypeCell");
    assertEquals(18, rows.size());
    //is first one about buttons
//    assertEquals("Buttons, Various uses of UIButton", rows.get(0).getAttribute("name"));
    //navigationBar is not inside table
    WebElement nav_bar = null;
    try {
      nav_bar = table.findElementByClassName("XCUIElementTypeNavigationBar");
    } catch (NoSuchElementException e) {
      //expected
    }
    assertNull(nav_bar);
    //there is nav bar inside the app
    driver.getPageSource();
    nav_bar = driver.findElementByClassName("XCUIElementTypeNavigationBar");
    assertNotNull(nav_bar);
  }


  @Test
  public void test_location() {
    //get third row location
    row = driver.findElementsByClassName("XCUIElementTypeCell").get(2);
    assertEquals(0, row.getLocation().getX());
    assertEquals(178, row.getLocation().getY());
  }

  @Test
  public void testScreenshot() {
    //make screenshot and get is as base64
    WebDriver augmentedDriver = new Augmenter().augment(driver);
    String screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.BASE64);

    assertNotNull(screenshot);
    //make screenshot and save it to the local filesystem
    File file = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
    assertNotNull(file);
  }

  @Test
  public void testTextFieldEdit() {
    //go to the text fields section
    openMenuPosition(13);
    WebElement text_field = driver.findElementsByClassName("UIATextField").get(0);
    //get default/empty text
    String default_val = text_field.getAttribute("value");
    //write some random text to element
    String rnd_string = RandomStringUtils.randomAlphanumeric(6);
    text_field.sendKeys(rnd_string);
    assertEquals(rnd_string, text_field.getAttribute("value"));
    //send some random keys
    String rnd_string2 = RandomStringUtils.randomAlphanumeric(6);
    Actions swipe = new Actions(driver).sendKeys(rnd_string2);
    swipe.perform();
    //check if text is there
    assertEquals(rnd_string + rnd_string2, text_field.getAttribute("value"));
    //clear
    text_field.clear();
    //check if is empty/has default text
    assertEquals(default_val, text_field.getAttribute("value"));
  }

  @Test
  public void testAlertInteraction() {
    //go to the alerts section
    openMenuPosition(10);

    //trigger modal alert with cancel & ok buttons
    List<IOSElement> triggerOkCancel = driver.findElementsByAccessibilityId("Okay / Cancel");
    triggerOkCancel.get(1).click();
    Alert alert = driver.switchTo().alert();
    //check if title of alert is correct
    assertEquals("UIAlertView <Alert message>", alert.getText());
    alert.accept();
  }

    @Test
    public void testScroll() {
        //scroll menu
        //get initial third row location
        row = driver.findElementsByClassName("XCUIElementTypeCell").get(2);
        Point location1 = row.getLocation();
        Point center = getCenter(row);
        scrollWithJS("down",1);       // driver.swipe(center.getX(), center.getY(), center.getX(), center.getY()-20, 1);
        Point location2 = row.getLocation();
        assertEquals(location1.getX(), location2.getX());
        assertNotEquals(location1.getY(), location2.getY());
    }

  @Test
  public void testSlider() {
    //go to controls
    openMenuPosition(10);
    //get the slider
    WebElement slider = driver.findElementByClassName("XCUIElementTypeSlider");
    assertEquals("42%", slider.getAttribute("value"));
//    Point sliderLocation = getCenter(slider);
//    //driver.swipe(sliderLocation.getX(), sliderLocation.getY(), sliderLocation.getX()-100, sliderLocation.getY(), 1);
//    assertEquals("0%", slider.getAttribute("value"));
  }


  @Test
  public void testSize() {
    Dimension table = driver.findElementByClassName("XCUIElementTypeTable").getSize();
    Dimension cell = driver.findElementsByClassName("XCUIElementTypeCell").get(0).getSize();
    assertEquals(table.getWidth(), cell.getWidth());
    assertNotSame(table.getHeight(), cell.getHeight());
  }

  @Test
  public void testSource() {
    //get main view soruce
    String source_main = driver.getPageSource();
    assertTrue(source_main.contains("XCUIElementTypeTable"));
    assertTrue(source_main.contains("TextFields, Uses of UITextField"));

    //got to text fields section
    openMenuPosition(2);
    String source_textfields = driver.getPageSource();
    assertTrue(source_textfields.contains("UIAStaticText"));
    assertTrue(source_textfields.contains("TextFields"));

    assertNotSame(source_main, source_textfields);
  }
}
