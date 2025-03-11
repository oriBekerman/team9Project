package il.cshaifasweng.OCSFMediatorExample.client;

import com.google.protobuf.StringValue;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchTablesReceivedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import javafx.css.StyleClass;
import javafx.css.Stylesheet;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.Math.min;

public class TableMapBoundary {

    public Pane outsideAreaPane;
    public Pane insideAreaPane;
    public Button tableBtn1;
    public Branch branch;
    public boolean mapIsSet=false;
    public GridPane insideGridPane;
    public Button tableBtn2;
    public Button tableBtn3;
    public Button checkBtn;
    private List<Button>buttons=new ArrayList<>();
    


    public TableMapBoundary()
    {
        EventBus.getDefault().register(this);
    }
    public void initialize() {
        if (branch != null) {
//            buttons=insideGridPane.getChildren();
//           setMap(branch);
        }
    }
    // initialize the map before letting the map page be opened
    public void setMap(Branch branch) {
        System.out.println("in set map before sync");
        synchronized (this) {
            if (branch != null)
            {
                System.out.println("in set map after sync");
                this.branch = branch;
                this.branch.tablesAreSet=false;
                System.out.println("after branch = " + this.branch);
                System.out.println("branch tables = " + String.valueOf(this.branch.tablesAreSet));
                System.out.println("Fetching tables for branch...");
                loadBranchTables();
                System.out.println("after load ...");
                try
                {
                    while (!branch.tablesAreSet) //wait for branch tables to be set in branch entity
                    {
                        wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread interrupted while waiting for tables.");
                    return;
                }
                // once the list of tables is loaded in branch make table buttons
                System.out.println("in set map after if branchTables:");
                System.out.println("map is set  = " + String.valueOf(mapIsSet));
                Set<RestTable> tables = branch.getTables(); // Assume this fetches the list of tables
                List<Button>buttons=new ArrayList<>();
                buttons.add(tableBtn1);
                buttons.add(tableBtn2);
                buttons.add(tableBtn3);
                List<RestTable> tableList = new ArrayList<>(tables); // Convert Set to List
                for (int i = 0; i < Math.min(tableList.size(), buttons.size()); i++) {
                    String num = String.valueOf(tableList.get(i).getId());
                    setButton(buttons.get(i), num);
                }
                this.mapIsSet = true;
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
    private void setButton(Button button,String num)
    {
        button.setText(num);
        button.setWrapText(true);
        button.setStyle(" -fx-font-size: 16px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: #8a6f48;\n" +
                "    -fx-alignment: center;\n" +
                "    -fx-padding: 8px 16px;\n" +
                "    -fx-border-radius: 6px;\n" +
                "    -fx-cursor: hand;");
    }

    //get the branch tables from the event client posted
    @Subscribe
    public void onBranchTablesEvent(BranchTablesReceivedEvent event) {
        synchronized (this) {
            Set<RestTable> tables = event.getTables();
            branch.setRestTables(tables);
            branch.tablesAreSet = true;

            System.out.println("Tables received! Notifying all waiting threads...");
            notifyAll();  // Wake up threads waiting for tables
        }
    }


    public void check(ActionEvent actionEvent) {
        Set<RestTable>tables=branch.getAvailableTablesAt(LocalTime.of(14,30));
        for(RestTable table: tables)
        {
            table.print();
        }
    }
}
