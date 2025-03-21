package com.example.myapplication;

import org.junit.Test;

// This sample code supports Appium Java client >=9
// https://github.com/appium/java-client
import io.appium.java_client.AppiumBy;
import io.appium.java_client.remote.options.BaseOptions;
import io.appium.java_client.android.AndroidDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

@RunWith(Parameterized.class)
public class ExampleUnitTest {
    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[10][0];
    }

    private AndroidDriver driver;
    Wait<AndroidDriver> wait;
    Wait<AndroidDriver> longWait;
    String formatGiftTileLoc = "//android.widget.FrameLayout[./android.widget.HorizontalScrollView//android.widget.TextView[contains(@text, 'Gifts')]]//androidx.recyclerview.widget.RecyclerView[.//*[contains(@text, 'These gifts')]]/android.widget.FrameLayout[%s]";

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

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);

        // Example usage of fluent wait
        wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);

        longWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofMinutes(10))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
    }

    public int giftIndex = 3;
    public By burgerLoc = AppiumBy.xpath("//android.widget.ImageView[@content-desc='Open navigation menu']");
    public By profileBtnLoc = AppiumBy.xpath("//android.widget.TextView[@text='My Profile']");
    public By giftsTabLoc = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Gifts')]");
    public By giftBtnLog = AppiumBy.xpath(String.format(formatGiftTileLoc, giftIndex));

    public By nonUniqUpgradeBtn = AppiumBy.xpath("//android.widget.TextView[@text='Non-Unique btn']");
    public By nonUniqUpgradeBtn2 = AppiumBy.xpath("//android.widget.TextView[@text='⭐ 500 ']");
    public By giftOkBtn = AppiumBy.xpath("//android.widget.Button[@content-desc='OK']");

    public By upgradeForBtn = AppiumBy.xpath("//android.widget.Button[contains(@content-desc, 'Upgrade for')]");
    public By senderNameLbl = AppiumBy.xpath("//android.widget.TextView[@text='Add sender's name to the gift']");

    public By collectibleLbl = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Collectible #')]");

    @Test
    public void sampleTest() throws InterruptedException {
        //Какой по счёту подарок на экране
        giftIndex = 3;
        System.out.println(driver.getBatteryInfo().getState());

        findElement(burgerLoc).click();
        findElement(profileBtnLoc).click();
        findElement(giftsTabLoc).click();
        findElement(giftBtnLog).click();

        boolean isUpgradeForOpened = false;
        int count = 0;
        By locToUse = nonUniqUpgradeBtn;
        while(!isUpgradeForOpened) {
            count++;
            findElement(locToUse).click();
            Thread.sleep(1000L);
            isUpgradeForOpened = !driver.findElements(upgradeForBtn).isEmpty();

            if (!isUpgradeForOpened) {
                closeGiftAndReopen();
            }

            //if (count == 5) locToUse = nonUniqUpgradeBtn;
        }

        findElement(upgradeForBtn).click();
        reClickUpgrade();

        longWait.until(ExpectedConditions.presenceOfElementLocated(collectibleLbl));
        System.out.println("НОМЕР ПОДАРКА: " + findElement(collectibleLbl).getText());
    }

    public void closeGiftAndReopen() throws InterruptedException {
        findElement(giftOkBtn).click();
        Thread.sleep(1000L);
        findElement(giftBtnLog).click();
    }

    public void reClickUpgrade() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(2000L);
            if (!driver.findElements(senderNameLbl).isEmpty()) {
                findElement(upgradeForBtn).click();
            }
        }
    }

    public WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
