package il.cshaifasweng.OCSFMediatorExample.client.Events;

public class ReportReceivedEvent {
    private final Object reportData;
    private final String reportType;  // Added to identify the type of report

    public ReportReceivedEvent(Object reportData, String reportType) {
        this.reportData = reportData;
        this.reportType = reportType;
    }

    public Object getReportData() {
        return reportData;
    }

    public String getReportType() {
        return reportType;
    }
}
