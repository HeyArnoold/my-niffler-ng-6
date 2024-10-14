package guru.qa.niffler.data.repository.impl.spingJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.repository.AuthUserRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        authAuthorityDao.create(user.getAuthorities().toArray(new AuthorityEntity[0]));
        return authUserDao.create(user);
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                    SELECT a.id as authority_id,
                                   authority,
                                   user_id as id,
                                   u.username,
                                   u.password,
                                   u.enabled,
                                   u.account_non_expired,
                                   u.account_non_locked,
                                   u.credentials_non_expired
                                   FROM "user" u join authority a on u.id = a.user_id WHERE u.id = ?
                                """,
                        AuthUserEntityExtractor.instance,
                        id
                )
        );
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                    SELECT a.id as authority_id,
                                   authority,
                                   user_id as id,
                                   u.username,
                                   u.password,
                                   u.enabled,
                                   u.account_non_expired,
                                   u.account_non_locked,
                                   u.credentials_non_expired
                                   FROM "user" u join authority a on u.id = a.user_id WHERE u.username = ?
                                """,
                        AuthUserEntityExtractor.instance,
                        username
                )
        );
    }

    @Override
    public void remove(AuthUserEntity user) {
        authUserDao.delete(user);
        authAuthorityDao.delete(user);
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        jdbcTemplate.update(con -> {
            PreparedStatement usersPs = con.prepareStatement(
                    "UPDATE \"user\" SET " +
                            "password = ?, " +
                            "enabled = ?, " +
                            "account_non_expired = ?, " +
                            "account_non_locked = ?, " +
                            "credentials_non_expired = ? " +
                            "WHERE id = ? "
            );

            usersPs.setString(1, user.getPassword());
            usersPs.setBoolean(2, user.getEnabled());
            usersPs.setBoolean(3, user.getAccountNonExpired());
            usersPs.setBoolean(4, user.getAccountNonLocked());
            usersPs.setBoolean(5, user.getCredentialsNonExpired());
            usersPs.setObject(6, user.getId());
            return usersPs;
        });
        return user;
    }
}
