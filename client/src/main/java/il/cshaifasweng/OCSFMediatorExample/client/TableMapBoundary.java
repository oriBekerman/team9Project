package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchTablesReceivedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import javafx.css.StyleClass;
import javafx.css.Stylesheet;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class TableMapBoundary {

    public Pane outsideAreaPane;
    public Pane insideAreaPane;
    public Button tableBtn1;
    public Button tableBtn2;
    public Button tableBtn3;
    public Branch branch;
    public boolean mapIsSet=false;


    public void initialize() {
        if (branch != null) {
           setMap(branch);
        }
    }

    //create table buttons for the map
    private Button makeTableBtn(RestTable table)
    {
        Button tableBtn = new Button();
        if(table.getCoordinates() != null)
        {
            tableBtn.setLayoutX(table.getCoordinates().getX());
            tableBtn.setLayoutY(table.getCoordinates().getY());
        }
        int num=table.getId();
        String tableId = Integer.toString(num);
        tableBtn.setText(tableId);
        switch (table.getCapacity()){
            case 2->tableBtn.setPrefSize(52,49);
            case 3->tableBtn.setPrefSize(77,74);
            case 4->tableBtn.setPrefSize(102,98);
            default -> tableBtn.setPrefSize(0,0);
        }
        tableBtn.setStyle("""
                    -fx-font-size: 18px;
                    -fx-font-weight: bold;
                    -fx-text-fill: white;
                    -fx-background-color: #8a6f48;
                    -fx-alignment: center;
                    -fx-padding: 10px 20px;
                    -fx-border-radius: 6px;
                    -fx-cursor: hand\
                """);
        return tableBtn;
    }
    // initialize the map before letting the map page be opened
    public void setMap(Branch branch) {
        System.out.println("in set map before sync");
        synchronized (this) {
            if (branch != null)
            {
                this.branch = branch;
                if (!branch.tablesAreSet)
                {
                    System.out.println("Fetching tables for branch...");
                    loadBranchTables();
                    return;  // Exit and wait for event to update tables
                }
                // once the list of tables is loaded in branch make table buttons
                for (RestTable table : branch.getTables()) {
                    makeTableBtn(table);
                }

                mapIsSet = true;
                System.out.println("Map is set");

                // Notify all waiting threads in openBranchMap()
                notifyAll();
            }
        }
    }

    //get branch tables from server
    private void loadBranchTables()
    {
        if (branch != null) {
            try {
                System.out.println("in load branches before fetch...");
                SimpleClient.getClient().fetchTables(branch);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //get the branch tables from the event client posted
    @Subscribe
    public void onBranchTablesEvent(BranchTablesReceivedEvent event) {
        synchronized (this) {
            List<RestTable> tables = event.getTables();
            branch.setRestTables(tables);
            branch.tablesAreSet = true;

            System.out.println("Tables received! Notifying all waiting threads...");
            notifyAll();  // Wake up threads waiting for tables
        }
    }




}
