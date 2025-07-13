package com.jalo.tenderstream.scraper;

import com.jalo.tenderstream.model.Tender;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TenderScraper implements AutoCloseable {

    /** تنسيق التاريخ كما يظهر في الموقع: 10/07/2025 10:00 */
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final By ROWS_LOCATOR = By.cssSelector("table tbody tr"); // عدّل لو امتلك الجدول ID محدّد

    private final WebDriver driver;

    public TenderScraper() {
        // شغّل Headless عند الحاجة
        ChromeOptions opts = new ChromeOptions();
        // opts.addArguments("--headless=new"); // فعّل هذا إذا شغّلت على خادم بلا واجهة
        driver = new ChromeDriver(opts);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    /** يرجع قائمة بكل المناقصات في الصفحة */
    public List<Tender> getTenders(String url) {
        driver.get(url);

        // Explicit wait حتى يظهر أول صف
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(ROWS_LOCATOR));

        List<WebElement> rows = driver.findElements(ROWS_LOCATOR);

        List<Tender> tenders = new ArrayList<>();
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() < 6) continue; // تخطّى الصفوف غير المكتملة

            try {
                tenders.add(parseRow(cells));
            } catch (Exception ex) {
                System.err.printf("⚠️  Failed to parse row (%s): %s%n",
                        cells.get(0).getText(), ex.getMessage());
            }
        }
        return tenders;
    }

    /** يحوّل خلايا الصف إلى كائن Tender */
    private Tender parseRow(List<WebElement> cells) {
        String number   = cells.get(0).getText().trim();
        String title    = cells.get(1).getText().trim();
        LocalDateTime start = LocalDateTime.parse(cells.get(2).getText().trim(), DATE_FMT);
        LocalDateTime end   = LocalDateTime.parse(cells.get(3).getText().trim(), DATE_FMT);
        String fee      = cells.get(4).getText().trim();
        String attribute= cells.get(5).getText().trim();

        return new Tender(number, title, start, end, fee, attribute);
    }

    @Override
    public void close() {
        driver.quit();
    }
}
