package guru.qa.niffler.data.repository.impl.spingJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.springJdbc.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;

@SuppressWarnings("resource")
public class UdUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

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
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement usersPs = con.prepareStatement(
                    "UPDATE public.user " +
                            "SET currency = ?, firstname = ?, surname = ?, photo = ?, photo_small = ?, full_name = ?" +
                            "WHERE id = ?"
            );

            usersPs.setString(1, user.getCurrency().name());
            usersPs.setString(2, user.getFirstname());
            usersPs.setString(3, user.getSurname());
            usersPs.setBytes(4, user.getPhoto());
            usersPs.setBytes(5, user.getPhotoSmall());
            usersPs.setString(6, user.getFullname());
            return usersPs;
        });
        return user;
    }

    @Override
    public void sendInvitation(UdUserEntity requester, UdUserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement friendshipPs = con.prepareStatement(
                    "INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)");

            friendshipPs.setObject(1, requester.getId());
            friendshipPs.setObject(2, addressee.getId());
            friendshipPs.setString(3, PENDING.name());
            return friendshipPs;
        });
        requester.addFriends(PENDING, addressee);
    }

    @Override
    public void addFriend(UdUserEntity requester, UdUserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement requesterPs = con.prepareStatement(
                    "INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)");

            requesterPs.setObject(1, requester.getId());
            requesterPs.setObject(2, addressee.getId());
            requesterPs.setString(3, ACCEPTED.name());
            return requesterPs;
        });

        jdbcTemplate.update(con -> {
            PreparedStatement addresseePs = con.prepareStatement(
                    "INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)");

            addresseePs.setObject(1, addressee.getId());
            addresseePs.setObject(2, requester.getId());
            addresseePs.setString(3, ACCEPTED.name());
            return addresseePs;
        });
        requester.addFriends(ACCEPTED, addressee);
        addressee.addFriends(ACCEPTED, requester);
    }

    @Override
    public void remove(UdUserEntity user) {
        // удаляем все данные из friendship
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update("DELETE FROM public.friendship WHERE requester_id = ?", user.getId());
        jdbcTemplate.update("DELETE FROM public.friendship WHERE addressee_id = ?", user.getId());

        // удаляем все данные из user
        udUserDao.delete(user);
    }
}
