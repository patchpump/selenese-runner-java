package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

/*
 * see: http://code.google.com/p/chromedriver/
 */
public class ChromeDriverFactory extends WebDriverFactory {

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        if (driverOptions.has(DriverOption.CHROMEDRIVER))
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, driverOptions.get(DriverOption.CHROMEDRIVER));
        // new ChromeDriver(Capabilities) is deprecated...
        ChromeOptions options = new ChromeOptions();
        if (driverOptions.has(DriverOption.PROXY))
            options.addArguments("--proxy-server=http://" + driverOptions.get(DriverOption.PROXY));
        return new ChromeDriver(options);
    }
}
