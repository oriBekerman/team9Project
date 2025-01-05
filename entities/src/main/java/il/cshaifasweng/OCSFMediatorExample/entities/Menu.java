package il.cshaifasweng.OCSFMediatorExample.entities;


import java.util.List;
import java.io.Serializable;

public class Menu implements Serializable {

    private String message;
    private List<MenuItem>menuItems;
    public Menu(List<MenuItem>items)
    {
        this.menuItems = items;
    }

    public List<MenuItem> getMenuItems()
    {
        return menuItems;
    }

    public void printMenu(){
        for(int i=0; i<=this.menuItems.size(); i++){
            menuItems.get(i).printMenuItem();
        }
    }
}