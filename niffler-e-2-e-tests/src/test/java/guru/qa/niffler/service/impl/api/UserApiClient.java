package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.impl.AuthApiClient;
import guru.qa.niffler.api.impl.UdUserApiClient;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.genRandomUsername;
import static java.util.Objects.requireNonNull;

@SuppressWarnings("DataFlowIssue")
@ParametersAreNonnullByDefault
public class UserApiClient implements UsersClient {

    private final AuthApiClient authApiClient = new AuthApiClient();
    private final UdUserApiClient udUserApiClient = new UdUserApiClient();

    @Override
    @Step("Создание нового пользователя с именем: {username}")
    public @Nullable UserJson createUser(String username, String password) {
        authApiClient.requestRegisterForm();
        authApiClient.registerUser(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        );
        UserJson createdUser = requireNonNull(udUserApiClient.getCurrentUser(username));
        return createdUser.addTestData(
                new TestData(
                        password,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
    }

    @Override
    @Step("Добавление {count} входящих приглашений пользователю: {targetUser.username}")
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        List<UserJson> incomeUsers = new ArrayList<>();

        if (count > 0) {
            UserJson user = udUserApiClient.getCurrentUser(targetUser.username());

            checkUserExist(user);

            for (int i = 0; i < count; i++) {
                UserJson newUser = createUser(genRandomUsername(), "12345");

                udUserApiClient.sendInvitation(newUser.username(), user.username());

                incomeUsers.add(newUser);
            }
        }
        return incomeUsers;
    }

    @Override
    public void createIncomeInvitations(UserJson userFrom, UserJson targetUser) {
        targetUser = udUserApiClient.getCurrentUser(targetUser.username());
        checkUserExist(targetUser);

        userFrom = udUserApiClient.getCurrentUser(userFrom.username());
        checkUserExist(userFrom);

        udUserApiClient.sendInvitation(userFrom.username(), targetUser.username());
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        List<UserJson> outcomeUsers = new ArrayList<>();

        if (count > 0) {
            UserJson user = udUserApiClient.getCurrentUser(targetUser.username());

            checkUserExist(user);

            for (int i = 0; i < count; i++) {
                UserJson newUser = createUser(genRandomUsername(), "12345");

                udUserApiClient.sendInvitation(user.username(), newUser.username());

                outcomeUsers.add(newUser);
            }
        }
        return outcomeUsers;
    }

    @Override
    public void createOutcomeInvitations(UserJson user, UserJson targetUser) {
        targetUser = udUserApiClient.getCurrentUser(targetUser.username());
        checkUserExist(targetUser);

        user = udUserApiClient.getCurrentUser(user.username());
        checkUserExist(user);

        udUserApiClient.sendInvitation(user.username(), targetUser.username());
    }

    @Override
    public List<UserJson> createFriends(UserJson targetUser, int count) {
        List<UserJson> friends = new ArrayList<>();

        if (count > 0) {
            // Шаг 1: Проверка, существует ли целевой пользователь (targetUser)
            UserJson user = udUserApiClient.getCurrentUser(targetUser.username());

            if (user == null || user.id() == null) {
                throw new AssertionError("Пользователь с именем " + targetUser.username() + " не найден");
            }

            for (int i = 0; i < count; i++) {
                // Шаг 2: Отправка входящего приглашения в друзья
                List<UserJson> incomeUsers = createIncomeInvitations(targetUser, 1);

                // Шаг 3: Принятие входящего приглашения в друзья
                udUserApiClient.acceptInvitation(user.username(), incomeUsers.getFirst().username());

                // Добавляем созданного друга в список
                friends.add(incomeUsers.getFirst());
            }
        }
        return friends;
    }

    @Override
    public void createFriends(UserJson user1, UserJson user2) {
        user1 = udUserApiClient.getCurrentUser(user1.username());
        checkUserExist(user1);

        user2 = udUserApiClient.getCurrentUser(user2.username());
        checkUserExist(user2);

        udUserApiClient.acceptInvitation(user1.username(), user2.username());
    }


    public @Nonnull List<UserJson> allUsers(String username, @Nullable String searchQuery) {
        return udUserApiClient.allUsers(username, searchQuery);
    }

    private void checkUserExist(UserJson user) {
        if (user.id() == null) {
            throw new AssertionError("Пользователь с именем " + user.username() + " не найден");
        }
    }
}
