package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass

public abstract class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String address;
    private String email;
    private String phone;


    // Default constructor (important for JPA)
    public Person() {}

    // Constructor with fields
    public Person(int id, String name, String address, String email) {
        this.id =id;
        this.name = name;
        this.address = address;
        this.email = email;
    }
    // Constructor with fields
    public Person(String name, String address, String email, String phone) {
        this.name = name;
        this.address = address;
        this.email = email;
    }
    // Constructor with fields
    public Person(String name, String email,String phoneNumber) {
        this.name = name;
        this.address = null;
        this.email = email;
        this.phone = phoneNumber;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
