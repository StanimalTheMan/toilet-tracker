package learn.toilet.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Review {

    private int reviewId;
    private int rating;
    private String reviewText;
    private Timestamp timeStamp;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate used;
    private int restroomId;
    private int userId;
    private String userName;
    private String locationName;

    public Review() {

    }

    public Review(int reviewId, int rating, String reviewText, Timestamp timeStamp, LocalDate used, int restroomId, int userId) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.timeStamp = timeStamp;
        this.used = used;
        this.restroomId = restroomId;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestroomId() {
        return restroomId;
    }

    public void setRestroomId(int restroomId) {
        this.restroomId = restroomId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDate getUsed() {
        return used;
    }

    public void setUsed(LocalDate used) {
        this.used = used;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}