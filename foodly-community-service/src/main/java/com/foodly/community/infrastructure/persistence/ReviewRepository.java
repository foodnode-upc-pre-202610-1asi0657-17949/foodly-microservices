package com.foodly.community.infrastructure.persistence;

import com.foodly.community.domain.model.Review;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ReviewRepository {

    @PersistenceContext(unitName = "FoodlyCommunityPU")
    private EntityManager em;

    public List<Review> findByHuariqueId(String huariqueId) {
        return em.createQuery("SELECT r FROM Review r WHERE r.huariqueId = :huariqueId", Review.class)
                .setParameter("huariqueId", huariqueId)
                .getResultList();
    }

    public Review save(Review review) {
        if (review.getId() == null) {
            em.persist(review);
            return review;
        } else {
            return em.merge(review);
        }
    }
}