package com.example.myapplication;

import androidx.annotation.NonNull;

import org.junit.Test;

// This sample code supports Appium Java client >=9
// https://github.com/appium/java-client
import io.appium.java_client.AppiumBy;
import io.appium.java_client.remote.options.BaseOptions;
import io.appium.java_client.android.AndroidDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    Process emulatorProcess;
    private AndroidDriver driver;
    Wait<AndroidDriver> wait;
    Wait<AndroidDriver> longWait;
    String formatGiftTileLoc = "//android.widget.FrameLayout[./android.widget.HorizontalScrollView//android.widget.TextView[contains(@text, 'Gifts')]]//androidx.recyclerview.widget.RecyclerView[.//*[contains(@text, 'These gifts')]]/android.widget.FrameLayout[%s]";

    public AndroidDriver getDriver() throws MalformedURLException {
        var options = new BaseOptions()
                .amend("noReset", "true")
                .amend("adbExecTimeout", "60000")
                .amend("forceAppLaunch", "true")
                .amend("platformName", "android")
                //.amend("appium:appActivity", "org.telegram.messenger.DefaultIcon")
                .amend("appium:appPackage", "org.telegram.messenger")
                .amend("appium:deviceName", "android")
                .amend("appium:automationName", "uiautomator2")
                .amend("appium:udid", "988a1b44364256345130")
                .amend("appium:ensureWebviewsHavePages", true)
                .amend("appium:nativeWebScreenshot", true)
                .amend("appium:newCommandTimeout", 3600)
                .amend("appium:connectHardwareKeyboard", true);

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
        wait = getWait();
        longWait = getLongWait();
        return driver;
    }

    public Wait<AndroidDriver> getLongWait() {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofMinutes(10))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class);
    }

    public Wait<AndroidDriver> getWait() {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(2))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class);
    }

    @Before
    public void setUp() throws IOException, InterruptedException {
        //emulatorProcess = runEmulator("emulator -avd Small_Phone3");
        //Thread.sleep(3000L);
        for (int i = 0; i < 5; i++) {
            if (driver != null) break;
            try {
                driver = getDriver();
            } catch (SessionNotCreatedException e) {
                System.out.println("Session not created, trying again");
                Thread.sleep(1000L);
            }
        }
    }

    public int giftIndex = 1;//Какой по счёту подарок на экране

    public By burgerLoc = AppiumBy.xpath("//android.widget.ImageView[@content-desc='Open navigation menu']");
    public By profileBtnLoc = AppiumBy.xpath("//android.widget.TextView[@text='My Profile']");
    public By giftsTabLoc = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Gifts')]");
    public By giftBtnLog = AppiumBy.xpath(String.format(formatGiftTileLoc, giftIndex));

    public By nonUniqUpgradeBtn = AppiumBy.xpath("//android.widget.TextView[@text='Non-Unique btn']");
    public By giftOkBtn = AppiumBy.xpath("//android.widget.Button[@content-desc='OK']");
    public By availabilityLbl = AppiumBy.xpath("//android.widget.TextView[contains (@text, ' left')]");

    public By upgradeForBtn = AppiumBy.xpath("//android.widget.Button[contains(@content-desc, 'Upgrade for')]");
    public By senderNameLbl = AppiumBy.xpath("//android.widget.TextView[@text=\"Add sender's name to the gift\"]");

    public By collectibleLbl = AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Collectible #')]");
    public By goBackBtn = AppiumBy.xpath("//android.widget.ImageView[@content-desc='Go back']");

    @Test
    public void sampleTest() throws InterruptedException {
        System.out.println(driver.getBatteryInfo().getState());

        openGiftFromStart();

        boolean isUpgradeForOpened = false;
        By locToUse = nonUniqUpgradeBtn;
        int count = 0;
        while(!isUpgradeForOpened) {
            count++;
            System.out.println("Cycle: " + count);

            if (!driver.findElements(locToUse).isEmpty()) {
                findElement(locToUse).click();
            }
            Thread.sleep(500L);
            isUpgradeForOpened = !driver.findElements(upgradeForBtn).isEmpty();

            if (!isUpgradeForOpened) {
                closeGiftAndReopen();
                isUpgradeForOpened = !driver.findElements(upgradeForBtn).isEmpty();
            }
        }

        findElement(upgradeForBtn).click();
        reClickUpgrade();

        longWait.until(ExpectedConditions.presenceOfElementLocated(collectibleLbl));
        System.out.println("НОМЕР ПОДАРКА: " + findElement(collectibleLbl).getText());
    }

    public void openGiftFromStart() {
        findElement(burgerLoc).click();
        findElement(profileBtnLoc).click();
        findElement(giftsTabLoc).click();
        findElement(giftBtnLog).click();
    }

    public void closeGiftAndReopen() throws InterruptedException {
        findElement(giftOkBtn).click();
        Thread.sleep(500L);
        findElement(goBackBtn).click();
        openGiftFromStart();
    }

    public void reClickUpgrade() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000L);
            if (!driver.findElements(senderNameLbl).isEmpty()) {
                findElement(upgradeForBtn).click();
            }
        }
    }

    public WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    @After
    public void tearDown() throws IOException, InterruptedException {
        try {
            driver.quit();
        } finally {
            //Process killProcess = new ProcessBuilder("adb", "-s", "emulator-5554", "emu", "kill").start();
            //killProcess.waitFor(); // Wait for the kill command to complete
            System.out.println("Emulator stopped successfully.");
        }
    }

    public Process runEmulator(String command) {
        Process process = null;
        try {
            // Create a ProcessBuilder for the command
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true); // Merge error stream with input stream

            // Start the process
            process = processBuilder.start();
            getThread(process);

        } catch (Exception e) {
            e.printStackTrace();
            process.destroy();
        }
        return process;
    }

    @NonNull
    private static void getThread(Process process) {
        Thread outputThread = new Thread(() -> {
            try (InputStream inputStream = process.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // Print the output in real-time
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start the output thread
        outputThread.start();
    }
}
