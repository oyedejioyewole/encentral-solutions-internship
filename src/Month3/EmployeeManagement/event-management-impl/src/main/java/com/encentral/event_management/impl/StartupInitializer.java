package com.encentral.event_management.impl;

import com.encentral.entities.JpaUser;
import com.encentral.entities.UserRole;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.util.UUID;

@Singleton
public class StartupInitializer {

    @Inject
    public StartupInitializer(JPAApi jpaApi) {
        jpaApi.withTransaction(() -> {
            EntityManager em = jpaApi.em();

            long count = em.createQuery(
                            "SELECT COUNT(u) FROM JpaUser u WHERE u.email = :email", Long.class)
                    .setParameter("email", "admin@encentral.com")
                    .getSingleResult();

            if (count == 0) {
                JpaUser admin = new JpaUser(
                        "admin@encentral.com",
                        "admin",
                        "Admin",
                        "User",
                        UserRole.ADMIN,
                        UUID.randomUUID().toString()
                );
                em.persist(admin);
                System.out.println("Default admin created: admin@encentral.com / admin");
            }
        });
    }
}