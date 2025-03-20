package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

// This sample code supports Appium Java client >=9
// https://github.com/appium/java-client
import io.appium.java_client.AppiumBy;
import io.appium.java_client.remote.options.BaseOptions;
import io.appium.java_client.android.AndroidDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;

public class ExampleUnitTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        var options = new BaseOptions()
                .amend("noReset", "true")
                .amend("forceAppLaunch", "true")
                .amend("platformName", "android")
                .amend("appium:appActivity", "org.telegram.messenger.DefaultIcon")
                .amend("appium:appPackage", "org.telegram.messenger.web")
                .amend("appium:deviceName", "android")
                .amend("appium:automationName", "uiautomator2")
                .amend("appium:udid", "emulator-5554")
                .amend("appium:ensureWebviewsHavePages", true)
                .amend("appium:nativeWebScreenshot", true)
                .amend("appium:newCommandTimeout", 3600)
                .amend("appium:connectHardwareKeyboard", true);

        driver = new AndroidDriver(this.getUrl(), options);
    }

    private URL getUrl() throws MalformedURLException {
        return new URL("http://127.0.0.1:4723");
    }

    public By burgerLoc = AppiumBy.xpath("//android.widget.ImageView[@content-desc='Open navigation menu']");
    public By profileBtnLoc = AppiumBy.xpath("//android.widget.TextView[@text='My Profile']");
    public By giftsTabLoc = AppiumBy.xpath("//android.widget.TextView[@text='Gifts x']");
    public By giftBtnLog = AppiumBy.xpath("//androidx.recyclerview.widget.RecyclerView[./android.widget.TextView[contains(@text, 'These gifts')]]/android.widget.FrameLayout");

    @Test
    public void sampleTest() throws InterruptedException {
        System.out.println(driver.getBatteryInfo().getState());

        driver.findElement(burgerLoc).click();
        Thread.sleep(1000L);
        driver.findElement(profileBtnLoc).click();
        Thread.sleep(1000L);
        driver.findElement(giftsTabLoc).click();
        Thread.sleep(1000L);
        driver.findElement(giftBtnLog).click();
        Thread.sleep(1000L);



    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
