package pageObject.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by saikrisv on 24/08/16.
 */
public class BaseTest {
    public AppiumDriver driver;
    DesiredCapabilities caps;
    public WebDriverWait wait;
    int height, width;

    @BeforeClass public void setUp() throws MalformedURLException {
        caps = new DesiredCapabilities();
        caps.setCapability(MobileCapabilityType.DEVICE_NAME, "android");
        caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, "5.0");
        caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "org.wordpress.android");
        caps.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY,
            "org.wordpress.android.ui.WPLaunchActivity");
        caps.setCapability(MobileCapabilityType.APP,
            System.getProperty("user.dir") + "/build/wordpress.apk");
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
        height = driver.manage().window().getSize().height;
        width =  driver.manage().window().getSize().width;
    }

    @AfterClass public void tearDown() {
        driver.quit();
    }

    public void waitForElement(AppiumDriver driver,MobileElement id){
        wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions
            .elementToBeClickable(id));
    }

    public void scrollWithTouchAction(AppiumDriver appiumDriver,String direction, int times) {
        TouchAction touchAction;
        touchAction = new TouchAction(appiumDriver);
        int startX = width / 2;
        int startY = height / 2 - 100;
        int endX = 0;
        int endY = height - 200;

        for (int i = 0; i < times; i++) {
            if (direction.equals("down")) {
                touchAction.press(startX, startY).moveTo(0, -endY).perform();
            } else if (direction.equals("up")) {
                touchAction.press(endX, endY).moveTo(startX, startY).perform();
            }
        }
    }
}
