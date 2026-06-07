package com.foodly.community.application.dto;

import com.foodly.community.domain.model.Review;
import java.time.LocalDateTime;

public class ReviewResponseDto {

    private Long id;
    private String customerUsername;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponseDto() {}

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.customerUsername = review.getCustomerUsername();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerUsername() { return customerUsername; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
