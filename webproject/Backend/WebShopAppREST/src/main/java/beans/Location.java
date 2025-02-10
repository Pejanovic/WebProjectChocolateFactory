package beans;

import java.util.Objects;

public class Location {
    private int id;
    private double longitude;
    private double latitude;
    private String street;
    private String city;
    private String postalCode;

    public Location() {}

    public Location(int id, double longitude, double latitude, String street, String city, String postalCode) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getFullAddress() {
        return street + ", " + city + ", " + postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.longitude, longitude) == 0 &&
                Double.compare(location.latitude, latitude) == 0 &&
                id == location.id &&
                Objects.equals(street, location.street) &&
                Objects.equals(city, location.city) &&
                Objects.equals(postalCode, location.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, longitude, latitude, street, city, postalCode);
    }
}
