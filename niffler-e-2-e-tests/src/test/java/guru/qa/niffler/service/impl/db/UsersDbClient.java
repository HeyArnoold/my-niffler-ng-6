package guru.qa.niffler.service.impl.db;

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
import guru.qa.niffler.service.UsersClient;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.util.*;

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

    @Override
    public Optional<UserJson> findById(UUID id) {
        return xaTransactionTemplate.execute(() -> {
                    Optional<UdUserEntity> byId = userdataUserRepository.findById(id);
                    return byId.map(entity -> UserJson.fromEntity(entity, null));
                }
        );
    }

    @Override
    public Optional<UserJson> findByUsername(String username) {
        return xaTransactionTemplate.execute(() -> {
                    Optional<UdUserEntity> byId = userdataUserRepository.findByUsername(username);
                    return byId.map(entity -> UserJson.fromEntity(entity, null));
                }
        );
    }

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
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        List<UserJson> incomeUsers = new ArrayList<>();
        if (count > 0) {
            UdUserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                String username = genRandomUsername();
                xaTransactionTemplate.execute(() -> {
                            UdUserEntity adressee = createNewUser(username, "12345");
                            userdataUserRepository.sendInvitation(adressee, targetEntity);
                            incomeUsers.add(UserJson.fromEntity(adressee, null));
                        }
                );
            }
        }
        return incomeUsers;
    }

    @Override
    public void createIncomeInvitations(UserJson userFrom, UserJson user) {
        UdUserEntity targetEntity = userdataUserRepository.findById(user.id()).orElseThrow();
        UdUserEntity userFromEntity = userdataUserRepository.findById(userFrom.id()).orElseThrow();

        xaTransactionTemplate.execute(() -> userdataUserRepository.sendInvitation(userFromEntity, targetEntity));
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        List<UserJson> outcomeInvitations = new ArrayList<>();
        if (count > 0) {
            UdUserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                String username = genRandomUsername();
                xaTransactionTemplate.execute(() -> {
                            UdUserEntity adressee = createNewUser(username, "12345");
                            userdataUserRepository.sendInvitation(targetEntity, adressee);
                            outcomeInvitations.add(UserJson.fromEntity(adressee, null));
                        }
                );
            }
        }
        return outcomeInvitations;
    }

    @Override
    public void createOutcomeInvitations(UserJson user, UserJson targetUser) {
        UdUserEntity userEntity = userdataUserRepository.findById(user.id()).orElseThrow();
        UdUserEntity targetEntity = userdataUserRepository.findById(targetUser.id()).orElseThrow();

        xaTransactionTemplate.execute(() -> userdataUserRepository.sendInvitation(targetEntity, userEntity));
    }

    @Override
    public List<UserJson> createFriends(UserJson targetUser, int count) {
        List<UserJson> addedFriends = new ArrayList<>();
        if (count > 0) {
            UdUserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                String username = genRandomUsername();
                xaTransactionTemplate.execute(() -> {
                            UdUserEntity adressee = createNewUser(username, "12345");
                            userdataUserRepository.addFriend(targetEntity, adressee);
                            addedFriends.add(UserJson.fromEntity(adressee, null));
                        }
                );
            }
        }
        return addedFriends;
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

    @Nonnull
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
