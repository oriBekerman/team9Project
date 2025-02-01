package il.cshaifasweng.OCSFMediatorExample.entities;
import javax.persistence.*;

@Entity
@Table(name = "Employees")

public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeID;

    @Column(name = "User Name")
    private String userName;
    private String userPassword;
    private String userAuthorization;

    public Employee(String userName, String userPassword, String userAuthorization) {
        super();
        this.userName = userName;
        this.userPassword = userPassword;
        this.userAuthorization = userAuthorization;
    }


    public int getEmployeeID() {
        return employeeID;
    }
    public void setEmployeeID(int itemID) {
        this.employeeID = itemID;
    }


    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserAuthorization() {
        return userAuthorization;
    }
    public void setUserAuthorization(String userAuthorization) {
        this.userAuthorization = userAuthorization;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String user_Name) {
        userName = user_Name;
    }

}