package il.cshaifasweng.OCSFMediatorExample.client.Events;

import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import java.util.List;
import java.util.Set;

public class BranchTablesReceivedEvent {
    private final Set<RestTable> tables;

    public BranchTablesReceivedEvent(Set<RestTable> tables) {
        this.tables = tables;
        System.out.println(" new tables event ");
    }

    public Set<RestTable> getTables() {
        return tables;
    }
}
