package il.cshaifasweng.OCSFMediatorExample.server.controllers;


import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.MenusRepository;
import org.hibernate.SessionFactory;


import java.util.ArrayList;
import java.util.List;

public class MenusController {

    private MenusRepository menusRepository;

    // constructor to inject the repository
    public MenusController(SessionFactory sessionFactory) {
        if(sessionFactory == null)
        {
            throw new NullPointerException(" in MenuController sessionFactory is null");
        }
        System.out.println("in MenuController constructor");
        this.menusRepository = new MenusRepository(sessionFactory);
    }
    public boolean checkIfEmpty()
    {
        return (menusRepository.checkIfEmpty());
    }
    public void populateMenus(List<Menu> menus)
    {
        if(checkIfEmpty())
        {

        }
        menusRepository.populate(menus);
    }
    public Menu getBaseMenu()
    {
        return menusRepository.getBaseMenu();
    }
    public void add(Menu menu)
    {
        menusRepository.add(menu);
    }
    public  Menu getBranchMenu(int branchID) {
        Menu menu;
        menu= menusRepository.getMenuByBranchID(branchID);
        return menu;
    }
}
