package com.encentral.image_inverter.impl;

import com.encentral.entities.JpaProcessedImage;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

import static com.encentral.entities.QJpaProcessedImage.jpaProcessedImage;

@Singleton
public class ProcessedImageRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Inject
    public ProcessedImageRepository(JPAQueryFactory queryFactory, EntityManager entityManager) {
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }

    public JpaProcessedImage save(JpaProcessedImage image) {
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(image);
            entityManager.getTransaction().commit();
            return image;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new RuntimeException("Failed to save processed image", e);
        }
    }

    public Optional<JpaProcessedImage> findById(UUID id) {
        JpaProcessedImage image = queryFactory
                .selectFrom(jpaProcessedImage)
                .where(jpaProcessedImage.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(image);
    }

    public Optional<JpaProcessedImage> findByFilePath(String filePath) {
        JpaProcessedImage image = queryFactory
                .selectFrom(jpaProcessedImage)
                .where(jpaProcessedImage.filePath.eq(filePath))
                .fetchOne();
        return Optional.ofNullable(image);
    }

    public boolean existsById(UUID id) {
        return queryFactory
                .selectFrom(jpaProcessedImage)
                .where(jpaProcessedImage.id.eq(id))
                .fetchFirst() != null;
    }

    public void deleteById(UUID id) {
        entityManager.getTransaction().begin();
        try {
            queryFactory
                    .delete(jpaProcessedImage)
                    .where(jpaProcessedImage.id.eq(id))
                    .execute();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new RuntimeException("Failed to delete processed image", e);
        }
    }
}