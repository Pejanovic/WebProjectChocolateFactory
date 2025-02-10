package beans;

import java.util.Objects;

import beans.ChocolateCategoryEnum.Category;
import beans.StatusChocolateEnum.Status;
import beans.TypeOfChocolateEnum.Type;

public class Chocolate {

    private int id;
    private String name;
    private double price;
    private int factoryId;
    private int grammage;
    private String description;
    private String image;
    private Category category;
    private Type type;
    private Status status;
    private int quantity;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public int getGrammage() {
        return grammage;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Category getCategory() {
        return category;
    }

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setFactoryId(int factoryId) {
        this.factoryId = factoryId;
    }

    public void setGrammage(int grammage) {
        this.grammage = grammage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public Chocolate() {
    }

    public Chocolate(int id, String name, double price, int factoryId, int grammage, String description, String image,
                     Category category, Type type, Status status, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.factoryId = factoryId;
        this.grammage = grammage;
        this.description = description;
        this.image = image;
        this.category = category;
        this.type = type;
        this.status = status;
        this.quantity = quantity;
    }

    public Chocolate(int id, String name, double price, int factoryId, int grammage, String description,
                     Category category, Type type, Status status, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.factoryId = factoryId;
        this.grammage = grammage;
        this.description = description;
        this.category = category;
        this.type = type;
        this.status = status;
        this.quantity = quantity;
    }
    
    public Chocolate(String name, double price, int factoryId, int grammage, String description, String image,
                     Category category, Type type, Status status, int quantity) {
        this.name = name;
        this.price = price;
        this.factoryId = factoryId;
        this.grammage = grammage;
        this.description = description;
        this.image = image;
        this.category = category;
        this.type = type;
        this.status = status;
        this.quantity = quantity;
    }

    public Chocolate(String name, double price, int factoryId, int grammage, String description,
                     Category category, Type type, Status status, int quantity) {
        this.name = name;
        this.price = price;
        this.factoryId = factoryId;
        this.grammage = grammage;
        this.description = description;
        this.category = category;
        this.type = type;
        this.status = status;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chocolate chocolate = (Chocolate) o;
        return Double.compare(chocolate.price, price) == 0 &&
                factoryId == chocolate.factoryId &&
                grammage == chocolate.grammage &&
                quantity == chocolate.quantity &&
                id == chocolate.id &&
                Objects.equals(name, chocolate.name) &&
                Objects.equals(description, chocolate.description) &&
                Objects.equals(image, chocolate.image) &&
                category == chocolate.category &&
                type == chocolate.type &&
                status == chocolate.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, factoryId, grammage, description, image, category, type, status, quantity);
    }
}
