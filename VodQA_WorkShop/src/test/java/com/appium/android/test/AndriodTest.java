package com.appium.android.test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import io.appium.java_client.touch.offset.PointOption;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
 * Code Snippet to run Appium Server programatically
 * Scroll/Swipe has been implemented
 * If running the test from windows please change the appium.js path @ line 39
 */

public class AndriodTest {

  private static final String Image_Scrollable = "org.wordpress.android:id/image_featured";
  public static final String PASSWORD = "org.wordpress.android:id/nux_password";
  public static final String USERNAME = "org.wordpress.android:id/nux_username";
  AppiumDriver driver;
  AppiumDriverLocalService appiumDriverLocalService;
  int height, width;

  @Before
  public void setUp() throws MalformedURLException {
//    AppiumServiceBuilder builder = new AppiumServiceBuilder()
//        .withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
//        .withArgument(GeneralServerFlag.LOG_LEVEL, "info").usingAnyFreePort(); /*and so on*/
//    ;
//    appiumDriverLocalService = builder.build();
//    appiumDriverLocalService.start();
    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability(MobileCapabilityType.DEVICE_NAME, "android");
    caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "org.wordpress.android");
    caps.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY,
        "org.wordpress.android.ui.WPLaunchActivity");
    caps.setCapability(MobileCapabilityType.APP,
        System.getProperty("user.dir") + "/build/wordpress.apk");
    caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
//		driver = new AndroidDriver<MobileElement>(appiumDriverLocalService.getUrl(), caps);
    driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), caps);
    height = driver.manage().window().getSize().height;
    width = driver.manage().window().getSize().width;
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

  @After
  public void tearDown() {
    driver.quit();
//    appiumDriverLocalService.stop();
  }

  //execute for android
  @SuppressWarnings("deprecation")
  @Test
  public void testLogin_Swipe_Scroll() throws InterruptedException {

    waitForElementClickable(By.id(USERNAME), 10);
    driver(By.id(USERNAME)).sendKeys("vodqa@gmail.com");
    driver(By.id(PASSWORD)).sendKeys("Hello12345678");
    driver(By.id("org.wordpress.android:id/nux_sign_in_button")).click();
    waitForElementClickable(By.id("switch_site"), 30);

    // Swipe Method_1
    Dimension size = driver.manage().window().getSize();
    int startx = (int) (size.width * 0.9);
    int endx = (int) (size.width * 0.20);
    int starty = size.height / 2;
    new TouchAction(driver).press(PointOption.point(startx, starty))
        .waitAction(Duration.ofSeconds(3))
        .moveTo(PointOption.point(endx, starty)).release().perform();
    waitForElementClickable(By.id(Image_Scrollable), 10);

    driver(By.id("org.wordpress.android:id/image_featured")).click();
    waitForElementClickable(By.id("org.wordpress.android:id/menu_browse"), 10);

    driver(By.id("org.wordpress.android:id/menu_browse")).click();
  }

  public WebElement driver(By by) {
    return driver.findElement(by);

  }

  public void waitForElementClickable(By by, int waitTime) {
    WebDriverWait wait = new WebDriverWait(driver, waitTime);
    wait.until(ExpectedConditions.elementToBeClickable(by));

  }

}
