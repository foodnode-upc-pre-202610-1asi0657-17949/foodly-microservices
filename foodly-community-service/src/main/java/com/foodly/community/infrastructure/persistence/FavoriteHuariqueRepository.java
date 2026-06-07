package com.foodly.community.infrastructure.persistence;

import com.foodly.community.domain.model.FavoriteHuarique;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class FavoriteHuariqueRepository {

    @PersistenceContext(unitName = "FoodlyCommunityPU")
    private EntityManager em;

    public List<FavoriteHuarique> findByCustomerUsername(String customerUsername) {
        return em.createQuery("SELECT f FROM FavoriteHuarique f WHERE f.customerUsername = :customerUsername", FavoriteHuarique.class)
                .setParameter("customerUsername", customerUsername)
                .getResultList();
    }

    public boolean existsByCustomerUsernameAndHuariqueId(String customerUsername, String huariqueId) {
        Long count = em.createQuery("SELECT COUNT(f) FROM FavoriteHuarique f WHERE f.customerUsername = :customerUsername AND f.huariqueId = :huariqueId", Long.class)
                .setParameter("customerUsername", customerUsername)
                .setParameter("huariqueId", huariqueId)
                .getSingleResult();
        return count > 0;
    }

    public void save(FavoriteHuarique favorite) {
        em.persist(favorite);
    }

    public void deleteByCustomerUsernameAndHuariqueId(String customerUsername, String huariqueId) {
        em.createQuery("DELETE FROM FavoriteHuarique f WHERE f.customerUsername = :customerUsername AND f.huariqueId = :huariqueId")
                .setParameter("customerUsername", customerUsername)
                .setParameter("huariqueId", huariqueId)
                .executeUpdate();
    }
}