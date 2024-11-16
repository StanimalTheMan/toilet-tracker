package learn.toilet.models;

public class Amenity {

    private int amenityId;
    private String amenityName;

    public Amenity() {
    }

    public Amenity(int amenityId, String amenityName) {
        this.amenityName = amenityName;
        this.amenityId = amenityId;
    }

    public int getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(int amenityId) {
        this.amenityId = amenityId;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }
}
