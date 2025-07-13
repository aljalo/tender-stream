package com.jalo.tenderstream.app;

import com.jalo.tenderstream.model.Tender;
import com.jalo.tenderstream.scraper.TenderScraper;
import com.jalo.tenderstream.sheets.GoogleSheetsService;

import java.util.List;

public class App {

    private static final String DEFAULT_URL = "https://eprocurement.petrochina-hfy.com"; // عدِّل عند الحاجة

    public static void main(String[] args) {
        String tendersUrl = args.length > 0 ? args[0] : DEFAULT_URL;
        System.out.printf("🔗 Using URL: %s%n", tendersUrl);

        try (TenderScraper scraper = new TenderScraper()) {

            List<Tender> tenders = scraper.getTenders(tendersUrl);
            System.out.printf("✅ Extracted %d tenders:%n", tenders.size());
            tenders.forEach(t ->
                    System.out.println(" • " + t.number() + " | " + t.title()));

            if (tenders.isEmpty()) {
                System.err.println("⚠️  No tenders found — skipping Google Sheet upload.");
                return;
            }

            // TODO: filter duplicates before upload (compare against Sheet if needed)

            System.out.println(">>> DEBUG: about to push to Google Sheet");
            GoogleSheetsService sheetService = new GoogleSheetsService();

            try {
                sheetService.appendTenders(tenders);
                System.out.println(">>> DEBUG: finished pushing to Google Sheet");
            } catch (Exception ex) {
                System.err.println("❌ Failed to upload to Google Sheet:");
                ex.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("❌ Fatal error:");
            e.printStackTrace();
        }
    }
}
