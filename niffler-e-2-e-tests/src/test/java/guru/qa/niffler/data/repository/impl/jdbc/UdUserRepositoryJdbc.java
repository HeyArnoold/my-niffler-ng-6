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
import static guru.qa.niffler.data.tpl.Connections.holder;

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
    public void addFriend(UdUserEntity requester, UdUserEntity addressee) {
        try (PreparedStatement requesterPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.friendship (requester_id, addressee_id, status) " +
                        "VALUES (?,?,?)");
             PreparedStatement addresseePs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)")) {

            requesterPs.setObject(1, requester.getId());
            requesterPs.setObject(2, addressee.getId());
            requesterPs.setString(3, ACCEPTED.name());
            requesterPs.executeUpdate();

            addresseePs.setObject(1, addressee.getId());
            addresseePs.setObject(2, requester.getId());
            addresseePs.setString(3, ACCEPTED.name());
            addresseePs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UdUserEntity user) {
        //todo возможно нужно еще удалять запросы в друзья от удаленного пользователя
        udUserDao.delete(user);
    }
}
