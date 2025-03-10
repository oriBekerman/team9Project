package il.cshaifasweng.OCSFMediatorExample.client;

import com.google.protobuf.StringValue;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchTablesReceivedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import javafx.css.StyleClass;
import javafx.css.Stylesheet;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableMapBoundary {

    public Pane outsideAreaPane;
    public Pane insideAreaPane;
    public Button tableBtn1;
    public Branch branch;
    public boolean mapIsSet=false;
    public GridPane insideGridPane;
    public Button tableBtn2;
    public Button tableBtn3;
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
//                for(RestTable table :branch.getTables())
//                {
//                    table.print();
//                }
//                buttons = insideGridPane.getChildren();
                System.out.println("map is set  = " + String.valueOf(mapIsSet));
                List<RestTable> tables = branch.getTables(); // Assume this fetches the list of tables
                tableBtn1.setText(String.valueOf(tables.getFirst().getId()));
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
    private void setButton(Button button,RestTable table)
    {
        int id=table.getId();
        String btnText= String.valueOf(id);
        button.setText(btnText);
        button.setWrapText(true);
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
