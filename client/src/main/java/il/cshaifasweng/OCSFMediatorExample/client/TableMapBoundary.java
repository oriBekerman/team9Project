package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import javafx.css.StyleClass;
import javafx.css.Stylesheet;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.List;

public class TableMapBoundary {

    public Pane outsideAreaPane;
    public Pane insideAreaPane;
    public Button tableBtn1;
    public Button tableBtn2;
    public Button tableBtn3;
    public Branch branch;

    public void initialize() {
        if (branch != null) {
            List<RestTable>tables = branch.getTables();
            for (RestTable table : tables) {
                makeTableBtn(table);
            }
        }
    }
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


}
