package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;
import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UdRepositoryHibernate implements UserdataUserRepository {

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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public UdUserEntity update(UdUserEntity user) {
        UdUserEntity toBeUpdated = findById(user.getId()).get();
        entityManager.joinTransaction();
        toBeUpdated.setCurrency(user.getCurrency());
        toBeUpdated.setFirstname(user.getFirstname());
        toBeUpdated.setSurname(user.getSurname());
        toBeUpdated.setPhoto(user.getPhoto());
        toBeUpdated.setPhotoSmall(user.getPhotoSmall());
        toBeUpdated.setFullname(user.getFullname());
        return entityManager.merge(toBeUpdated);
    }

    @Override
    public void sendInvitation(UdUserEntity requester, UdUserEntity addressee) {
        entityManager.joinTransaction();
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(PENDING);
        friendship.setCreatedDate(new Date());
        entityManager.persist(friendship);
        requester.addFriends(PENDING, addressee);
    }

    @Override
    public void addFriend(UdUserEntity user1, UdUserEntity user2) {
        entityManager.joinTransaction();
        Date date = new Date();
        FriendshipEntity requesterFriendship = new FriendshipEntity();
        requesterFriendship.setRequester(user1);
        requesterFriendship.setAddressee(user2);
        requesterFriendship.setStatus(ACCEPTED);
        requesterFriendship.setCreatedDate(date);

        FriendshipEntity addresseeFriendship = new FriendshipEntity();
        addresseeFriendship.setRequester(user2);
        addresseeFriendship.setAddressee(user1);
        addresseeFriendship.setStatus(ACCEPTED);
        addresseeFriendship.setCreatedDate(date);

        entityManager.persist(requesterFriendship);
        entityManager.persist(addresseeFriendship);

        user1.addFriends(ACCEPTED, user2);
        user2.addFriends(ACCEPTED, user1);
    }

    @Override
    public void remove(UdUserEntity user) {
        entityManager.joinTransaction();
        entityManager.remove(user);
    }
}
