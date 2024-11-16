package learn.toilet.models;

import java.util.ArrayList;
import java.util.List;

public class Restroom {

    private int restroomId;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private String directions;
    private String description;
    private List<Review> reviews = new ArrayList<>();
    private List<RestroomAmenity> amenities = new ArrayList<>();
    private int userId;

    public Restroom() {
    }

    public Restroom(int restroomId, String name, double latitude, double longitude, String address, String directions, String description, int userId) {
        this.restroomId = restroomId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.directions = directions;
        this.description = description;
        this.userId = userId;
    }

    public int getRestroomId() {
        return restroomId;
    }

    public void setRestroomId(int restroomId) {
        this.restroomId = restroomId;
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

    public List<Review> getReviews() {
        return new ArrayList<>(reviews);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<RestroomAmenity> getAmenities() {
        return new ArrayList<>(amenities);
    }

    public void setAmenities(List<RestroomAmenity> amenities) {
        this.amenities = amenities;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
