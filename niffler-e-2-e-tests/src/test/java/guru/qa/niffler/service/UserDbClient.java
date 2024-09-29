package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaConsumer;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class UserDbClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**
     * Создаем пользователя распределенной транзакцией в DB niffler-auth и niffler-userdata
     */
    public UserJson createUser(UserJson user) {
        UdUserEntity userEntity = xaTransaction(
                new XaFunction<>(connection -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    new AuthUserDaoJdbc(connection).create(authUser);
                    new AuthAuthorityDaoJdbc(connection).create(createAuthorityArray(authUser));
                    return null;
                }, CFG.authJdbcUrl()),
                new XaFunction<>(connection -> {
                    UdUserEntity ue = new UdUserEntity();
                    ue.setUsername(user.username());
                    ue.setFullname(user.fullname());
                    ue.setCurrency(user.currency());

                    return new UdUserDaoJdbc(connection).create(ue);
                }, CFG.userdataJdbcUrl())
        );
        return UserJson.fromEntity(userEntity, null);
    }

    /**
     * Удаляем пользователя распределенной транзакцией в DB niffler-auth и niffler-userdata
     */
    public void deleteUser(UserJson user) {
        xaTransaction(new XaConsumer(connection -> new AuthUserDaoJdbc(connection)
                        .findByUsername(user.username())
                        .ifPresent(
                                authUser -> {
                                    new AuthAuthorityDaoJdbc(connection).delete(createAuthorityArray(authUser));
                                    new AuthUserDaoJdbc(connection).delete(authUser);
                                }
                        ), CFG.authJdbcUrl()),
                new XaConsumer(connection -> new UdUserDaoJdbc(connection)
                        .findByUsername(user.username())
                        .ifPresent(
                                ue -> new UdUserDaoJdbc(connection).delete(ue)
                        ), CFG.userdataJdbcUrl()));
    }


    /**
     * Создаем массив AuthorityEntity со всеми вариантами Authority (read, write)
     */
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
