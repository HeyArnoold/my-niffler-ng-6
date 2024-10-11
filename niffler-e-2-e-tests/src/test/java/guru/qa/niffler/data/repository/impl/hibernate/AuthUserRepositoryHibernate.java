package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(AuthUserEntity.class, id)
        );
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        String queryStr = "select u from AuthUserEntity u where u.username =: username";
        try {
            return Optional.of(
                    entityManager.createQuery(queryStr, AuthUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.remove(user);
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        //todo Должны ли мы здесь ограничивать изменение бизнес-ключей по аналогии как мы сделали в jdbc и springJdbc репозиториях? Например через createQuery
        entityManager.joinTransaction();
        return entityManager.merge(user);
    }
}
