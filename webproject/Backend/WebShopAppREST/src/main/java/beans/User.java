package beans;

import java.time.LocalDate;

import beans.CustomerTypeEnum.CustomerType;
import beans.UserTypeEnum.UserType;

public class User {

    int id;
    String username;
    String password;
    String name;
    String lastName;
    LocalDate birthday;
    double points;
    int factoryId;
    CustomerType customerType;
    UserType userType;
    String gender;
    boolean suspicious;
    boolean blocked;

    public User(String username, String password, String name, String lastName, LocalDate birthday, UserType userType, String gender) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.birthday = birthday;
        this.userType = userType;
        this.gender = gender;
    }

    public User(int id, String username, String password, String name, String lastName, LocalDate birthday,
                UserType userType, double points, int factoryId, CustomerType customerType, String gender, boolean suspicious, boolean blocked) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.birthday = birthday;
        this.userType = userType;
        this.points = points;
        this.factoryId = factoryId;
        this.customerType = customerType;
        this.gender = gender;
        this.suspicious = suspicious;
        this.blocked = blocked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(int factoryId) {
        this.factoryId = factoryId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
