package guru.qa.niffler.data.repository.impl.spingJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.springJdbc.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;

public class UdUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

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
        String sql = "UPDATE public.user SET currency = ?, firstname = ?, surname = ?, photo = ?, photo_small = ?, full_name = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                user.getCurrency().name(),
                user.getFirstname(),
                user.getSurname(),
                user.getPhoto(),
                user.getPhotoSmall(),
                user.getFullname()
        );
        return user;
    }

    @Override
    public void sendInvitation(UdUserEntity requester, UdUserEntity addressee) {
        jdbcTemplate.update("INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)",
                requester.getId(),
                addressee.getId(),
                PENDING.name()
        );
        requester.addFriends(PENDING, addressee);
    }

    @Override
    public void addFriend(UdUserEntity user1, UdUserEntity user2) {
        jdbcTemplate.update("INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)",
                user1.getId(),
                user2.getId(),
                ACCEPTED.name()
        );

        jdbcTemplate.update("INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)",
                user2.getId(),
                user1.getId(),
                ACCEPTED.name()
        );
        user1.addFriends(ACCEPTED, user2);
        user2.addFriends(ACCEPTED, user1);
    }

    @Override
    public void remove(UdUserEntity user) {
        // удаляем все данные из friendship
        jdbcTemplate.update("DELETE FROM public.friendship WHERE requester_id = ?", user.getId());
        jdbcTemplate.update("DELETE FROM public.friendship WHERE addressee_id = ?", user.getId());

        // удаляем все данные из user
        udUserDao.delete(user);
    }
}
