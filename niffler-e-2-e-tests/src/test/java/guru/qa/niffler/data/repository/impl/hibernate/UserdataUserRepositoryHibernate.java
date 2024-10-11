package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Override
    public UdUserEntity create(UdUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(UdUserEntity.class, id)
        );
    }

    @Override
    public Optional<UdUserEntity> findByUsername(String username) {
        String queryStr = "select u from UdUserEntity u where u.username =: username";
        try {
            return Optional.of(
                    entityManager.createQuery(queryStr, UdUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public UdUserEntity update(UdUserEntity user) {
        //todo Должны ли мы здесь ограничивать изменение бизнес-ключей по аналогии как мы сделали в jdbc и springJdbc репозиториях? Например через createQuery
        entityManager.joinTransaction();
        return entityManager.merge(user);
    }

    @Override
    public void sendInvitation(UdUserEntity requester, UdUserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.PENDING, addressee);
    }

    @Override
    public void addFriend(UdUserEntity requester, UdUserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    }

    @Override
    public void remove(UdUserEntity user) {
        entityManager.joinTransaction();
        entityManager.remove(user);
    }
}
