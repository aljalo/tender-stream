package com.jalo.tenderstream.sheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.jalo.tenderstream.model.Tender;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "TenderStream";
    private static final String SPREADSHEET_ID   = "1LymBtbZJMifAzbx8sTOmMGjl9bphCxbve9JuARDdDHU";
    private static final String SHEET_NAME       = "Tenders";

    /** تنسيق الكتابة في الشيت */
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Sheets sheets;

    public GoogleSheetsService() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("credentials.json");
        if (in == null)
            throw new IllegalStateException("credentials.json not found in resources!");

        var credential = GoogleCredential.fromStream(in)
                .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));

        sheets = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        ensureSheetExists();
    }

    /* ------------------------------------------------------------------ */
    /* -------------  الواجهة العلنية لإضافة المناقصات  ------------------ */
    /* ------------------------------------------------------------------ */

    public void appendTenders(List<Tender> tenders) throws Exception {

        // 1) جلب الأرقام الموجودة
        Set<String> existingNumbers = getExistingTenderNumbers();
        List<List<Object>> rowsToAdd = new ArrayList<>();

        // 2) بناء الصفوف غير المكررة
        for (Tender t : tenders) {
            if (existingNumbers.contains(t.number())) {
                System.out.printf("⏩  Skipping duplicate: %s%n", t.number());
                continue;
            }
            rowsToAdd.add(List.of(
                    t.number(),
                    t.title(),
                    t.startingDate().format(DF),
                    t.closingDate().format(DF),
                    t.fee(),
                    t.attribute()
            ));
        }

        if (rowsToAdd.isEmpty()) {
            System.out.println("⚠️  No **new** tenders to add.");
            return;
        }

        // 3) إرسال الصفوف الجديدة دفعة واحدة
        ValueRange body = new ValueRange().setValues(rowsToAdd);
        AppendValuesResponse result = sheets.spreadsheets().values()
                .append(SPREADSHEET_ID, SHEET_NAME + "!A:F", body)
                .setValueInputOption("USER_ENTERED")
                .execute();

        System.out.printf("✅  Added %d new rows to Google Sheet.%n",
                result.getUpdates().getUpdatedRows());
    }

    /* ------------------------------------------------------------------ */
    /* ----------------------  الدوال المساعدة  -------------------------- */
    /* ------------------------------------------------------------------ */

    /** يقرأ أرقام المناقصات الموجودة في العمود A ويُرجِعها كمجموعة */
    private Set<String> getExistingTenderNumbers() throws Exception {
        ValueRange resp = sheets.spreadsheets().values()
                .get(SPREADSHEET_ID, SHEET_NAME + "!A2:A")
                .execute();

        Set<String> nums = new HashSet<>();
        List<List<Object>> values = resp.getValues();
        if (values != null) {
            for (List<Object> row : values) {
                if (!row.isEmpty())
                    nums.add(row.get(0).toString().trim());
            }
        }
        System.out.printf("📊  Found %d existing numbers in sheet.%n", nums.size());
        return nums;
    }

    /** يتأكد من وجود ورقة باسم SHEET_NAME، وإلا يُنشئها */
    private void ensureSheetExists() throws Exception {
        Spreadsheet ss = sheets.spreadsheets().get(SPREADSHEET_ID).execute();
        boolean exists = ss.getSheets().stream()
                .anyMatch(sh -> SHEET_NAME.equals(sh.getProperties().getTitle()));

        if (!exists) {
            AddSheetRequest add = new AddSheetRequest()
                    .setProperties(new SheetProperties().setTitle(SHEET_NAME));
            BatchUpdateSpreadsheetRequest req = new BatchUpdateSpreadsheetRequest()
                    .setRequests(List.of(new Request().setAddSheet(add)));
            sheets.spreadsheets().batchUpdate(SPREADSHEET_ID, req).execute();
            System.out.println("🆕  Created sheet tab \"" + SHEET_NAME + "\"");
        }
    }
}





//package com.jalo.tenderstream.sheets;
//
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.model.*;
//import com.jalo.tenderstream.model.Tender;
//
//import java.io.InputStream;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//public class GoogleSheetsService {
//
//    private static final String APPLICATION_NAME = "TenderStream";
//    private static final String SPREADSHEET_ID  = "1LymBtbZJMifAzbx8sTOmMGjl9bphCxbve9JuARDdDHU";
//    private static final String SHEET_NAME      = "Tenders";
//
//    /** تنسيق الكتابة في الشيت */
//    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//    private final Sheets sheets;
//
//    public GoogleSheetsService() throws Exception {
//        InputStream in = getClass().getClassLoader().getResourceAsStream("credentials.json");
//        if (in == null) throw new IllegalStateException("credentials.json not found!");
//
//        var credential = GoogleCredential.fromStream(in)
//                .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));
//
//        sheets = new Sheets.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                JacksonFactory.getDefaultInstance(),
//                credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//
//        ensureSheetExists();
//    }
//
//    /** يضيف المناقصات كلها دفعة واحدة */
//    public void appendTenders(List<Tender> tenders) throws Exception {
//
//        // TODO: filter duplicates here (compare with existing numbers)
//
//        List<List<Object>> rows = new ArrayList<>(tenders.size());
//        for (Tender t : tenders) {
//            rows.add(List.of(
//                    t.number(),
//                    t.title(),
//                    t.startingDate().format(DF),
//                    t.closingDate().format(DF),
//                    t.fee(),
//                    t.attribute()
//            ));
//        }
//
//        var body   = new ValueRange().setValues(rows);
//        var result = sheets.spreadsheets().values()
//                .append(SPREADSHEET_ID, SHEET_NAME + "!A:F", body)
//                .setValueInputOption("USER_ENTERED")   // يعالج التاريخ كقيمة تاريخ
//                .execute();
//
//        System.out.printf("✅ Added %d rows to Google Sheet.%n",
//                result.getUpdates().getUpdatedRows());
//    }
//
//    /** يتأكد أن ورقة "Tenders" موجودة، ويُنشئها إن لزم الأمر */
//    private void ensureSheetExists() throws Exception {
//        var spreadsheet = sheets.spreadsheets().get(SPREADSHEET_ID).execute();
//        boolean exists = spreadsheet.getSheets().stream()
//                .anyMatch(sh -> SHEET_NAME.equals(sh.getProperties().getTitle()));
//
//        if (!exists) {
//            AddSheetRequest add = new AddSheetRequest()
//                    .setProperties(new SheetProperties().setTitle(SHEET_NAME));
//            BatchUpdateSpreadsheetRequest req = new BatchUpdateSpreadsheetRequest()
//                    .setRequests(List.of(new Request().setAddSheet(add)));
//            sheets.spreadsheets().batchUpdate(SPREADSHEET_ID, req).execute();
//            System.out.println("🆕 Created sheet tab \"" + SHEET_NAME + "\"");
//        }
//    }
//}
//
