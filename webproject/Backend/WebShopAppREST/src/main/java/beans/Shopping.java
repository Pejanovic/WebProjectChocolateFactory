package beans;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;

import beans.ShoppingStatusEnum.ShopStatusEnum;

public class Shopping {

    private String idShopping;
    private int factoryId;

    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date date;

    private double price;
    private String nameUser;
    private ShopStatusEnum status;
    private boolean canceled;
    private LocalDate canceledDate;
    private List<Chocolate> chocolates;
    private List<Integer> amounts;
    private String comment;

    public Shopping() {}

    public Shopping(String idShopping, int factoryId, Date date, double price, String nameUser, ShopStatusEnum status,
                    boolean canceled, LocalDate canceledDate, List<Chocolate> chocolates, List<Integer> amounts, String comment) {
        this.idShopping = idShopping;
        this.factoryId = factoryId;
        this.date = date;
        this.price = price;
        this.nameUser = nameUser;
        this.status = status;
        this.canceled = canceled;
        this.canceledDate = canceledDate;
        this.chocolates = chocolates;
        this.amounts = amounts;
        this.comment = comment;
    }

    public Shopping(int factoryId, Date date, double price, String nameUser, ShopStatusEnum status,
                    boolean canceled, LocalDate canceledDate, List<Chocolate> chocolates, List<Integer> amounts, String comment) {
        this.factoryId = factoryId;
        this.date = date;
        this.price = price;
        this.nameUser = nameUser;
        this.status = status;
        this.canceled = canceled;
        this.canceledDate = canceledDate;
        this.chocolates = chocolates;
        this.amounts = amounts;
        this.comment = comment;
    }

    public Shopping(int factoryId, double price, String nameUser, List<Chocolate> chocolates, List<Integer> amounts, String comment) {
        this.factoryId = factoryId;
        this.date = new Date();
        this.price = price;
        this.nameUser = nameUser;
        this.status = ShopStatusEnum.PROCESSING;
        this.canceled = false;
        this.canceledDate = null;
        this.chocolates = chocolates;
        this.amounts = amounts;
        this.comment = comment;
    }

    public List<Integer> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Integer> amounts) {
        this.amounts = amounts;
    }

    public String getIdShopping() {
        return idShopping;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public Date getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public String getNameUser() {
        return nameUser;
    }

    public ShopStatusEnum getStatus() {
        return status;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public LocalDate getCanceledDate() {
        return canceledDate;
    }

    public List<Chocolate> getChocolates() {
        return chocolates;
    }

    public void setIdShopping(String idShopping) {
        this.idShopping = idShopping;
    }

    public void setFactoryId(int factoryId) {
        this.factoryId = factoryId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public void setStatus(ShopStatusEnum status) {
        this.status = status;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setCanceledDate(LocalDate canceledDate) {
        this.canceledDate = canceledDate;
    }

    public void setChocolates(List<Chocolate> chocolates) {
        this.chocolates = chocolates;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
