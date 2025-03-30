package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Menu implements Serializable {
    private int menuID;
    private List<MenuItem> menuItems;//hust until i figure out where to change things to getFullList
    public Menu(List<MenuItem>items) //figure out how to use because currently items is not a column in Menus table
    {
        this.menuItems = items;
    }
    public Menu () {};
    public int getMenuID() {
        return menuID;
    }
    public void setMenuItems(List<MenuItem> menuItems)
    {
        this.menuItems = menuItems;
    }
    public void printMenu(){
        for (MenuItem menuItem : this.menuItems)
        {
            menuItem.printMenuItem();
        }
    }
    public MenuItem getItemByID(int itemID)
    {
       return menuItems.get(itemID);
    }
    public void SetMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    public boolean isMenuEmpty()
    {
        if(menuItems==null)
            return true;
        boolean i= menuItems.isEmpty();
        System.out.println(i);
        return i;
    }
    public List<MenuItem> getMenuItems()
    {
        return menuItems;
    }

}
