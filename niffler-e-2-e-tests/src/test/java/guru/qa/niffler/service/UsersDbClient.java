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
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UdUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UdUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDaoSpring = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpring = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDaoSpring = new UdUserDaoSpringJdbc();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
    private final UdUserRepository udUserRepository = new UdUserRepositoryJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl());

    @SuppressWarnings("unchecked")
    public UserJson createUser(UserJson user, String password) {
        return UserJson.fromEntity(
                xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = createAuthUserEntityFromUserJson(user, password);

                    AuthUserEntity createdAuthUser = authUserDaoSpring.create(authUser);
                    authAuthorityDaoSpring.create(createAuthorityArray(createdAuthUser));

                    UdUserEntity udUserEntity = UdUserEntity.fromJson(user);
                    return udUserDaoSpring.create(udUserEntity);
                }),
                null);
    }

    @SuppressWarnings("unchecked")
    public UserJson createUserByRepo(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(username);
                    authUser.setPassword(pe.encode(password));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);
                    authUser.setAuthorities(
                            Arrays.stream(Authority.values()).map(
                                    e -> {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        ae.setUser(authUser);
                                        ae.setAuthority(e);
                                        return ae;
                                    }
                            ).toList()
                    );
                    authUserRepository.create(authUser);

                    UdUserEntity ue = new UdUserEntity();
                    ue.setUsername(username);
                    ue.setCurrency(CurrencyValues.RUB);
                    ue.setFirstname(null);
                    ue.setSurname(null);
                    ue.setFullname(null);
                    ue.setPhoto(null);
                    ue.setPhotoSmall(null);

                    ue = udUserRepository.create(ue);

                    return UserJson.fromEntity(ue, null);
                }
        );
    }

    public void addIncomeInvitation(UserJson user, UserJson requester) {
        UdUserEntity userEntity = UdUserEntity.fromJson(user);
        UdUserEntity requesterEntity = UdUserEntity.fromJson(requester);

        udUserRepository.addInvitation(requesterEntity, userEntity);
    }

    public void addOutcomeInvitation(UserJson user, UserJson requestTarget) {
        UdUserEntity userEntity = UdUserEntity.fromJson(user);
        UdUserEntity requesterEntity = UdUserEntity.fromJson(requestTarget);

        udUserRepository.addInvitation(userEntity, requesterEntity);
    }

    public void addFriends(UserJson user, UserJson friendToAdd) {
        UdUserEntity userEntity = UdUserEntity.fromJson(user);
        UdUserEntity friendToAddEntity = UdUserEntity.fromJson(friendToAdd);

        xaTransactionTemplate.execute(() ->
                udUserRepository.addFriend(userEntity, friendToAddEntity)
        );
    }

    public void deleteUser(String username) {
        xaTransactionTemplate.execute(() -> {
            authUserDaoSpring.findByUsername(username)
                    .ifPresent(
                            authUser -> {
                                authAuthorityDaoSpring.delete(authUser);
                                authUserDaoSpring.delete(authUser);
                            }
                    );
            udUserDaoSpring.findByUsername(username)
                    .ifPresent(udUserDaoSpring::delete);
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

    private AuthUserEntity createAuthUserEntityFromUserJson(UserJson user, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        return authUser;
    }
}
