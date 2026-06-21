package com.foodly.community.application.service;

import com.foodly.community.application.dto.ReviewRequestDto;
import com.foodly.community.application.dto.ReviewResponseDto;
import com.foodly.community.domain.model.FavoriteHuarique;
import com.foodly.community.domain.model.Review;
import com.foodly.community.infrastructure.persistence.FavoriteHuariqueRepository;
import com.foodly.community.infrastructure.persistence.ReviewRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommunityService {

    @Inject
    private ReviewRepository reviewRepository;

    @Inject
    private FavoriteHuariqueRepository favoriteRepository;

    public List<ReviewResponseDto> getHuariqueReviews(String huariqueId) {
        return reviewRepository.findByHuariqueId(huariqueId)
                .stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDto addReview(String huariqueId, String customerUsername, ReviewRequestDto request) {
        Review review = new Review(huariqueId, customerUsername, request.getRating(), request.getComment());
        reviewRepository.save(review);
        return new ReviewResponseDto(review);
    }

    @Transactional
    public void addFavorite(String customerUsername, String huariqueId) {
        if (!favoriteRepository.existsByCustomerUsernameAndHuariqueId(customerUsername, huariqueId)) {
            FavoriteHuarique favorite = new FavoriteHuarique(customerUsername, huariqueId);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFavorite(String customerUsername, String huariqueId) {
        favoriteRepository.deleteByCustomerUsernameAndHuariqueId(customerUsername, huariqueId);
    }

    public List<String> getCustomerFavorites(String customerUsername) {
        return favoriteRepository.findByCustomerUsername(customerUsername)
                .stream()
                .map(FavoriteHuarique::getHuariqueId)
                .collect(Collectors.toList());
    }
}