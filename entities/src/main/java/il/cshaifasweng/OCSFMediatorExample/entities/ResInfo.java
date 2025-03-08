package il.cshaifasweng.OCSFMediatorExample.entities;
import javax.persistence.*;
import javax.persistence.*;
import java.io.Serializable;

// old version without the Customer + Person entities considerations :
@Entity
@Table(name ="resSInfo",uniqueConstraints = {@UniqueConstraint(columnNames = "ID")})
public class ResInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "ID", unique = true, nullable = false)
    private Integer resID;

    @Column
    private String fullName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String mail;

    @Column(nullable = false)
    private String creditCardNum;

    @Column(nullable = false)
    private String creditCardED;

    @Column(nullable = false)
    private int creditCardCVV;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String hours;

    @Column(nullable = false)
    private int numOfGuests;

    @Column(nullable = false)
    private String inOrOut;


    // Constructor with all fields except resID (auto-generated)
    public ResInfo(String fullName, String phoneNumber, String mail, String creditCardNum, String creditCardED, int creditCardCVV, String date, String hours, int numOfGuests, String inOrOut) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.creditCardNum = creditCardNum;
        this.creditCardED = creditCardED;
        this.creditCardCVV = creditCardCVV;
        this.date = date;
        this.hours =hours;
        this.numOfGuests = numOfGuests;
        this.inOrOut = inOrOut;
    }

    public ResInfo() {
        // בנאי ריק נדרש על ידי Hibernate
    }


    //Getters and Setters
    public Integer getResID() {return resID;}
    public void setResID(Integer resID) {this.resID = resID;}

    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}

    public String getPhoneNumber() {return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

    public String getMail() {return mail;}
    public void setMail(String mail) {this.mail = mail;}

    public String getCreditCardNum() {return creditCardNum;}
    public void setCreditCardNum(String creditCardNum) {this.creditCardNum = creditCardNum;}

    public String getCreditCardED() {return creditCardED;}
    public void setCreditCardED(String creditCardED) {this.creditCardED = creditCardED;}

    public int getCreditCardCVV() {return creditCardCVV;}
    public void setCreditCardCVV(int creditCardCVV) {this.creditCardCVV = creditCardCVV;}

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public String getHours() {return hours;}
    public void setHours(String hours) {this.hours = hours;}

    public int getNumOfGuests() {return numOfGuests;}
    public void setNumOfGuests(int numOfGuests) {this.numOfGuests = numOfGuests;}

    public String getInOrOut() {return inOrOut;}
    public void setInOrOut(String inOrOut) {this.inOrOut = inOrOut;}

    public void printResInfo(){
        System.out.println("ResInfo{" +
                "resID=" + this.resID +
                ", fullName='" + this.fullName + '\'' +
                ", phoneNumber='" + this.phoneNumber + '\'' +
                ", mail='" + this.mail + '\'' +
                ", creditCardNum='" + this.creditCardNum + '\'' +
                ", creditCardED='" + this.creditCardED + '\'' +
                ", creditCardCVV=" + this.creditCardCVV +
                ", date='" + this.date + '\'' +
                ", hours='" + this.hours + '\'' +
                ", numOfGuests=" + this.numOfGuests +
                ", inOrOut='" + this.inOrOut + '\'' +
                '}');
    }
}







