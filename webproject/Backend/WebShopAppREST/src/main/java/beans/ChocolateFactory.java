package beans;

import java.util.List;
import java.util.Objects;

import beans.ChocolateFactoryStatusEnum.StatusType;

public class ChocolateFactory {
    private int id; 
    private String name;
    private List<Chocolate> chocolatesOffered;
    private String workingHours;
    private StatusType status; 
    private int locationId;
    private String logo;
    private int managerId;
    private double rating;
    private boolean deleted; 

    public ChocolateFactory() {}

    public ChocolateFactory(String name, List<Chocolate> chocolatesOffered, String workingHours, StatusType status, int locationId, String logo, double rating, int managerId) {
        this.name = name;
        this.chocolatesOffered = chocolatesOffered;
        this.workingHours = workingHours;
        this.status = status;
        this.locationId = locationId;
        this.logo = logo;
        this.rating = rating;
        this.managerId = managerId;
        this.deleted = false;
    }

    public ChocolateFactory(int id, String name, List<Chocolate> chocolatesOffered, String workingHours, StatusType status, int locationId, String logo, double rating, int managerId) {
        this.id = id;
        this.name = name;
        this.chocolatesOffered = chocolatesOffered;
        this.workingHours = workingHours;
        this.status = status;
        this.locationId = locationId;
        this.logo = logo;
        this.rating = rating;
        this.managerId = managerId;
        this.deleted = false;
    }

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

    public List<Chocolate> getChocolatesOffered() {
        return chocolatesOffered;
    }

    public void setChocolatesOffered(List<Chocolate> chocolatesOffered) {
        this.chocolatesOffered = chocolatesOffered;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChocolateFactory that = (ChocolateFactory) o;
        return id == that.id &&
                Double.compare(that.rating, rating) == 0 &&
                deleted == that.deleted &&
                locationId == that.locationId &&
                managerId == that.managerId &&
                Objects.equals(name, that.name) &&
                Objects.equals(chocolatesOffered, that.chocolatesOffered) &&
                Objects.equals(workingHours, that.workingHours) &&
                status == that.status &&
                Objects.equals(logo, that.logo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, chocolatesOffered, workingHours, status, locationId, logo, rating, managerId, deleted);
    }
}
