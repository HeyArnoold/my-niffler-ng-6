package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UdRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static guru.qa.niffler.utils.RandomDataUtils.genRandomUsername;


public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UdRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    return UserJson.fromEntity(
                            userdataUserRepository.create(userEntity(username)),
                            null
                    );
                }
        );
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UdUserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = genRandomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UdUserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.sendInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    public void createIncomeInvitations(UserJson userFrom, UserJson user) {
        UdUserEntity userEntity = userdataUserRepository.findById(user.id()).orElseThrow();
        UdUserEntity userFromEntity = userdataUserRepository.findById(userFrom.id()).orElseThrow();

        xaTransactionTemplate.execute(() -> userdataUserRepository.sendInvitation(userFromEntity, userEntity));
    }

    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UdUserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = genRandomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UdUserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.sendInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    public void createOutcomeInvitations(UserJson user, UserJson targetUser) {
        UdUserEntity userEntity = userdataUserRepository.findById(user.id()).orElseThrow();
        UdUserEntity targetUserEntity = userdataUserRepository.findById(targetUser.id()).orElseThrow();

        xaTransactionTemplate.execute(() -> userdataUserRepository.sendInvitation(userEntity, targetUserEntity));
    }

    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            UdUserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = genRandomUsername();
                            userdataUserRepository.addFriend(targetEntity, createNewUser(username, "12345"));
                        }
                );
            }
        }
    }

    @Override
    public void createFriends(UserJson user1, UserJson user2) {
        UdUserEntity userEntity1 = userdataUserRepository.findById(user1.id()).orElseThrow();
        UdUserEntity userEntity2 = userdataUserRepository.findById(user2.id()).orElseThrow();

        xaTransactionTemplate.execute(() -> userdataUserRepository.addFriend(userEntity1, userEntity2));
    }

    @Override
    public void deleteUser(String username) {
        xaTransactionTemplate.execute(() -> {
            authUserRepository.findByUsername(username)
                    .ifPresent(
                            authUserRepository::remove
                    );
            userdataUserRepository.findByUsername(username)
                    .ifPresent(userdataUserRepository::remove);
        });
    }

    private UdUserEntity createNewUser(String username, String password) {
        AuthUserEntity authUser = authUserEntity(username, password);
        authUserRepository.create(authUser);
        return userdataUserRepository.create(userEntity(username));
    }

    private UdUserEntity userEntity(String username) {
        UdUserEntity ue = new UdUserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
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
        return authUser;
    }
}
