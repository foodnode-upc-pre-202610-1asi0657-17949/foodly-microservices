package com.foodly.identity.infrastructure.persistence;

import com.foodly.identity.domain.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class UserRepository {

    @PersistenceContext(unitName = "FoodlyIdentityPU") // Se conecta al persistence.xml de tu servicio
    private EntityManager em;

    // Reemplaza al findByEmail de Spring Data
    public Optional<User> findByEmail(String email) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // Reemplaza al findByUsername de Spring Data
    public Optional<User> findByUsername(String username) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // Reemplaza al existsByEmail de Spring Data
    public boolean existsByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    // Reemplaza al existsByUsername de Spring Data
    public boolean existsByUsername(String username) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    // Reemplaza al findByActiveTrueOrderByCreatedAtDesc de Spring Data
    public List<User> findByActiveTrueOrderByCreatedAtDesc() {
        return em.createQuery("SELECT u FROM User u WHERE u.active = true ORDER BY u.createdAt DESC", User.class)
                .getResultList();
    }

    // Métodos CRUD básicos que Spring te daba gratis y ahora los manejamos nativos:

    public User save(User user) {
        // Si el usuario ya tiene un ID asignado o existe, lo actualiza (merge), si no, lo inserta (persist)
        if (em.contains(user) || user.getId() != null) {
            return em.merge(user);
        } else {
            em.persist(user);
            return user;
        }
    }

    public Optional<User> findById(String id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }
}