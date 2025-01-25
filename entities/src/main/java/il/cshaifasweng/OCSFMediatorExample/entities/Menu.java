package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.util.List;
import java.io.Serializable;

//@Entity
//@Table(name = "Menu")

public class Menu implements Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int menuID;
//
//    @Column(nullable = false)
//    private int branchID;

    private List<MenuItem>menuItems;

    public Menu(List<MenuItem>items)
    {
        this.menuItems = items;
    }

    public Menu () {};

//    public int getId() {
//        return menuID;
//    }
    
    public List<MenuItem> getMenuItems()
    {
        return menuItems;
    }

    public void printMenu(){
        for(int i=0; i<this.menuItems.size(); i++){
            menuItems.get(i).printMenuItem();
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
        boolean I= menuItems.isEmpty();
        System.out.println(I);
        return I;
    }
}
