package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import java.util.List;

public class BranchTablesReceivedEvent {
    private final List<RestTable> tables;

    public BranchTablesReceivedEvent(List<RestTable> tables) {
        this.tables = tables;
        System.out.println(" new tables event ");
    }

    public List<RestTable> getTables() {
        return tables;
    }
}
