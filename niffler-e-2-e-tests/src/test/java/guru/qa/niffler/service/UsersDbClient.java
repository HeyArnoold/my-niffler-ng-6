package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.authJdbcUrl(), CFG.userdataJdbcUrl());


    @SuppressWarnings("unchecked")
    public UserJson createUser(UserJson user) {
        return UserJson.fromEntity(
                xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDao.create(authUser);
                    authAuthorityDao.create(createAuthorityArray(createdAuthUser));

                    return udUserDao.create(UdUserEntity.fromJson(user));
                }),
                null);
    }

    public void deleteUser(UserJson user) {
        xaTransactionTemplate.execute(() -> {
            authUserDao.findByUsername(user.username())
                    .ifPresent(
                            authUser -> {
                                authAuthorityDao.delete(authUser);
                                authUserDao.delete(authUser);
                            }
                    );
            udUserDao.findByUsername(user.username())
                    .ifPresent(udUserDao::delete);
            return null;
        });
    }

    private AuthorityEntity[] createAuthorityArray(AuthUserEntity authUser) {
        return Arrays.stream(Authority.values())
                .map(authority -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(authority);
                            return ae;
                        }
                ).toArray(AuthorityEntity[]::new);
    }
}
