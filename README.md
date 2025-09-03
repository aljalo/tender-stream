
# Tender Stream

**Automated Tender Scraping and Google Sheets Synchronization**

---

## About

**Tender Stream** is a Java-based automation tool designed to scrape tender information from specified sources and synchronize the data into Google Sheets. The application ensures data integrity by implementing deduplication logic, preventing duplicate entries in the spreadsheet.

---

## Features

* **Automated Tender Scraping**: Efficiently extracts tender details from predefined websites or APIs.
* **Google Sheets Integration**: Syncs scraped data directly into Google Sheets for easy access and management.
* **Deduplication Logic**: Automatically identifies and removes duplicate entries to maintain data accuracy.
* **Configurable Filters**: Allows customization of scraping parameters to target specific tender information.
* **Scheduled Execution**: Supports cron jobs or scheduled tasks for periodic data scraping and synchronization.

---

## Technologies Used

* **Java 21**: Core programming language for backend development.
* **Selenium WebDriver**: Automates web browser interaction for scraping dynamic content.
* **Google Sheets API**: Facilitates communication between the application and Google Sheets.
* **Maven**: Manages project dependencies and build processes.
* **Cron Jobs/Scheduled Tasks**: Automates the execution of scraping tasks at specified intervals.

---
```
tender-stream/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── tenderstream/
│   │   │           ├── App.java                    # Main entry point
│   │   │           ├── scraper/                    # All scraping logic
│   │   │           │   ├── TenderScraper.java
│   │   │           │   └── ScraperUtils.java
│   │   │           ├── sheets/                     # Google Sheets integration
│   │   │           │   ├── GoogleSheetsService.java
│   │   │           │   └── SheetUtils.java
│   │   │           ├── models/                     # Data models
│   │   │           │   └── Tender.java
│   │   │           ├── auth/                       # Authentication logic (if needed)
│   │   │           │   └── LinkedInAuthenticator.java
│   │   │           └── utils/                      # Utility classes
│   │   │               └── DeduplicationUtils.java
│   │   └── resources/
│   │       └── credentials/                        # Google service account JSON
│   │           └── careerpilot-sheets-c4624e233bec.json
├── pom.xml                                           # Maven dependencies
├── README.md                                        # Project documentation
├── .gitignore                                       # Files/folders to ignore in Git
└── LICENSE                                          # Optional license

```
---

## Installation & Usage

### Prerequisites

* Java Development Kit (JDK) 21 or higher
* Maven build automation tool
* Google Service Account with access to Google Sheets API
* ChromeDriver compatible with your Chrome browser version

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/aljalo/tender-stream.git
   cd tender-stream
   ```

2. Install project dependencies:

   ```bash
   mvn clean install
   ```

3. Configure Google Sheets API credentials:

   * Place your `careerpilot-sheets-c4624e233bec.json` file inside the `src/main/resources/` directory.

4. Set up environment variables for LinkedIn credentials:

   * `LINKEDIN_EMAIL`: Your LinkedIn email address.
   * `LINKEDIN_PASSWORD`: Your LinkedIn password.

5. Modify the `GoogleSheetsService.java` file to set your desired `SPREADSHEET_ID` and `RANGE` for data insertion.

6. Run the application:

   ```bash
   mvn exec:java -Dexec.mainClass="com.linkedinjobbot.App"
   ```

---

## Security Considerations

* **Credentials**: Never hardcode sensitive information like passwords or API keys in your codebase. Use environment variables or secure vaults.
* **Google Sheets API**: Ensure that your service account has the necessary permissions to access and modify the target Google Sheet.
* **LinkedIn Login**: Be cautious with LinkedIn credentials. Consider using OAuth or other secure authentication methods.

---

## Future Enhancements

* **Automated Job Applications**: Extend functionality to apply for jobs directly through LinkedIn.
* **Notification System**: Implement email or SMS notifications for new tender postings.
* **Data Analytics**: Analyze scraped data to provide insights into tender trends and opportunities.
* **User Interface**: Develop a GUI for easier configuration and monitoring of the scraping process.

---
