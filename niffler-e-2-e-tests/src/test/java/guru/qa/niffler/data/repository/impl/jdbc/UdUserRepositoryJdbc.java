package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.UdUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;
import static guru.qa.niffler.data.jdbc.Connections.holder;

@SuppressWarnings("resource")
public class UdUserRepositoryJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final UdUserDao udUserDao = new UdUserDaoJdbc();

    @Override
    public UdUserEntity create(UdUserEntity user) {
        return udUserDao.create(user);
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        return udUserDao.findById(id);
    }

    @Override
    public Optional<UdUserEntity> findByUsername(String username) {
        return udUserDao.findByUsername(username);
    }

    @Override
    public UdUserEntity update(UdUserEntity user) {
        try (PreparedStatement usersPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE public.user " +
                        "SET currency = ?, firstname = ?, surname = ?, photo = ?, photo_small = ?, full_name = ?" +
                        "WHERE id = ?")
        ) {

            usersPs.setString(1, user.getCurrency().name());
            usersPs.setString(2, user.getFirstname());
            usersPs.setString(3, user.getSurname());
            usersPs.setBytes(4, user.getPhoto());
            usersPs.setBytes(5, user.getPhotoSmall());
            usersPs.setString(6, user.getFullname());
            usersPs.executeUpdate();

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendInvitation(UdUserEntity requester, UdUserEntity addressee) {
        try (PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)")) {

            friendshipPs.setObject(1, requester.getId());
            friendshipPs.setObject(2, addressee.getId());
            friendshipPs.setString(3, PENDING.name());
            friendshipPs.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UdUserEntity user1, UdUserEntity user2) {
        try (PreparedStatement requesterPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.friendship (requester_id, addressee_id, status) " +
                        "VALUES (?,?,?)");
             PreparedStatement addresseePs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)")) {

            requesterPs.setObject(1, user1.getId());
            requesterPs.setObject(2, user2.getId());
            requesterPs.setString(3, ACCEPTED.name());
            requesterPs.executeUpdate();

            addresseePs.setObject(1, user2.getId());
            addresseePs.setObject(2, user1.getId());
            addresseePs.setString(3, ACCEPTED.name());
            addresseePs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UdUserEntity user) {
        // удаляем все данные из friendship
        try (PreparedStatement requesterPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM public.friendship WHERE requester_id = ?");
             PreparedStatement addresseePs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM public.friendship WHERE addressee_id = ?")
        ) {
            requesterPs.setObject(1, user.getId());
            requesterPs.executeUpdate();

            addresseePs.setObject(1, user.getId());
            addresseePs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // удаляем все данные из user
        udUserDao.delete(user);
    }
}
