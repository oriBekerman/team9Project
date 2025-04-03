package il.cshaifasweng.OCSFMediatorExample.server.reports;

public abstract class Report {
    protected int branchId;
    protected Object data;

    public Report(int branchId) {
        this.branchId = branchId;
    }

    public abstract void fetchData();
    public abstract Object generateReportData();
}