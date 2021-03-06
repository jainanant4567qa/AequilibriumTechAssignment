package Utilities;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static Reporting.ExtentManager.getTest;
import static TestSetup.TestSetup.webDriver;

public class TestUtils {

    /**
     * Explict wait till element is visible on the screen
     *
     * @param element
     * @param time
     */
    public static void waitTillElementIsDisplayed(WebElement element, int time) {
            WebDriverWait wait = new WebDriverWait(webDriver, time);
            wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Implicit wait
     *
     * @param timeout
     */
    public static void implicitWait(int timeout) {
            webDriver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
    }

    /**
     * Return boolean value for element's visibilty on the screen
     *
     * @param element
     * @param time
     * @return
     */
    public static boolean isElementPresent(WebElement element, int time) {
        try {
            waitTillElementIsDisplayed(element, time);
            element.isDisplayed();
            return true;
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    /**
     * Add Screenshot in Report
     *
     * @param path
     * @return Screenshot from path
     */
    public static Media addScreenshotInReport(String path) {
        return MediaEntityBuilder.createScreenCaptureFromPath("." + path).build();
    }

    /**
     * Click on element
     *
     * @param element
     * @param buttonName
     */
    public static void clickOnElement(WebElement element, String buttonName) {
        waitTillElementIsDisplayed(element, 20);
        try {
            element.click();
            Log.info("Tap on " + buttonName);
            getTest().log(Status.PASS, "Tap on " + buttonName);
        } catch (Exception e) {
                Log.error(e.getMessage());
                getTest().log(Status.FAIL, e.getMessage(), addScreenshotInReport(captureScreenshot()));

        }
    }

    /**
     * Clear Textfield
     *
     * @param element
     * @param fieldName
     */

    public static void clearText(WebElement element, String fieldName) {
        try {
            Log.info("Clearing text from " + fieldName);
            element.clear();
            getTest().log(Status.PASS, "Cleared text from : " + fieldName, addScreenshotInReport(captureScreenshot()));
        } catch (Exception e) {
            Log.error(" Failed to clear text : " + " in " + fieldName);
            getTest().log(Status.FAIL, e.getMessage(), addScreenshotInReport(captureScreenshot()));
        }
    }

    /**
     * Clear Textfield and enter text
     *
     * @param element
     * @param textToEnter
     * @param fieldName
     */
    public static void clearAndEnterText(WebElement element, String textToEnter, String fieldName) {
        try {
            Log.info("Clearing text from " + fieldName);
            element.clear();
            Log.info("Entering text : " + textToEnter + " in " + fieldName);
            element.sendKeys(textToEnter);
            getTest().log(Status.PASS, "Entered " + textToEnter + " in " + fieldName,
                    addScreenshotInReport(captureScreenshot()));
        } catch (Exception e) {
            Log.error("Failed to enter text : " + textToEnter + " in " + fieldName);
            getTest().log(Status.FAIL, e.getMessage(), addScreenshotInReport(captureScreenshot()));
        }
    }

    /**
     * Verify the text on element
     *
     * @param element
     * @param expectedText
     */
    public static void textVerification(WebElement element, String expectedText) {
        waitTillElementIsDisplayed(element, 20);
        try {
            if (element.isDisplayed()) {
                if (StringUtils.normalizeSpace(element.getText()).equals(expectedText)) {
                    Log.info("Element is displayed, \n Element Text is " + "'" + element.getText() + "'");
                    getTest().log(Status.PASS,
                            "Element is displayed, \n Element Text is " + "'" + element.getText() + "'",
                            addScreenshotInReport(captureScreenshot()));
                } else {
                    Log.error("Expected Text is " + "'" + expectedText + "'" + " but got " + "'" + element.getText() + "'");
                    getTest().log(Status.FAIL, "Expected Text is " + "'" + expectedText + "'" + " " + "but got " +
                            "'" + element.getText() + "'", addScreenshotInReport(captureScreenshot()));
                }
            }
        } catch (NoSuchElementException e) {
            Log.error(e.getMessage());
            getTest().log(Status.FAIL, e.getMessage(), addScreenshotInReport(captureScreenshot()));
        }
    }

    /**
     * Capture Screenshot inside Reports/Screenshot folder
     *
     * @return Screenshot path
     */
    public static String captureScreenshot() {

        TakesScreenshot ts = null;
        if (webDriver != null) {
            ts = (TakesScreenshot) webDriver;
        }
        File src = ts.getScreenshotAs(OutputType.FILE);
        String path = "./Reports/Screenshots/screenShot_" + System.currentTimeMillis() + ".PNG";
        File destination = new File(path);
        try {
            FileUtils.copyFile(src, destination);
        } catch (IOException e) {
            System.out.println("Capture Failed " + e.getMessage());
        }
        return path;
    }

    /**
     * Get value from Testdata Json file , available in Testdata folder
     *
     * @param key
     * @return
     * @throws IOException
     */
    public static String getValueFromJson(String key) throws IOException {
        return com.jayway.jsonpath.JsonPath.read(TestSetup.TestSetup.jsonTestData, "$." + key);
    }

    /**
     * Get value from Expected Result Json file , available in Testdata folder
     *
     * @param key
     * @return
     * @throws IOException
     */
    public static String getMessagesFromJson(String key) throws IOException {
        return com.jayway.jsonpath.JsonPath.read(TestSetup.TestSetup.jsonExpectedResults, "$." + key);
    }
}
