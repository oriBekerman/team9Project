package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

//@Entity
//@Table(name = "Menus")

public class Menu implements Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int menuID;

//    @OneToOne
//    @JoinColumn(name = "branch_id", referencedColumnName = "id", nullable = false)
//    private Branch branch;

//    @ManyToOne
//    @JoinColumn(name = "base_menu_id") // Foreign key pointing to the base menu
//    private Menu baseMenu;
//
//    @ManyToMany
//    @JoinTable(
//            name = "menu_additional_items",
//            joinColumns = @JoinColumn(name = "menu_id"),
//            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
//    )
//    private List<MenuItem> additionalMenuItems;
//

//    @Transient
    private List<MenuItem> menuItems;//hust until i figure out where to change things to getFullList

    public Menu(List<MenuItem>items) //figure out how to use because currently items is not a column in Menus table
    {
        this.menuItems = items;
    }

    public Menu () {};

    public int getMenuID() {
        return menuID;
    }

//    public Branch getBranch() {
//        return branch;
//    }
//
//    public void setBranch(Branch branch) {
//        this.branch = branch;
//    }
//
//    public Menu getBaseMenu() {
//        return baseMenu;
//    }
//
//    public void setBaseMenu(Menu baseMenu) {
//        this.baseMenu = baseMenu;
//    }
//
//    public List<MenuItem> getAdditionalMenuItems() {
//        return additionalMenuItems;
//    }
//
//    public void setAdditionalMenuItems(List<MenuItem> additionalMenuItems) {
//        this.additionalMenuItems = additionalMenuItems;
//    }
//
//    // Combine Base Items and Additional Items
//    @Transient
//    public List<MenuItem> getFullMenuItems() {
//        List<MenuItem> fullMenu = baseMenu != null ? baseMenu.getFullMenuItems() : new ArrayList<>();
//        if(additionalMenuItems != null)
//        {
//            fullMenu.addAll(additionalMenuItems);
//        }
//        return fullMenu;
//    }

    public void printMenu(){
        for (MenuItem menuItem : this.menuItems) {
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

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
}
