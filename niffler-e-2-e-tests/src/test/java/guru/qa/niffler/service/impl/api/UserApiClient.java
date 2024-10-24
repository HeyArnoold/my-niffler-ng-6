package guru.qa.niffler.service.impl.api;

import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserApiClient implements UsersClient {

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserApi authUserApi = retrofit.create(UserApi.class);

    @Override
    public Optional<UserJson> findById(UUID id) {
        // todo
        return Optional.empty();
    }

    @Override
    public Optional<UserJson> findByUsername(String username) {
        // todo
        return Optional.empty();
    }

    @Override
    public @Nullable UserJson createUser(String username, String password) {
        // todo
        return null;
    }

    @Override
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        // todo
        return null;
    }

    @Override
    public void createIncomeInvitations(UserJson userFrom, UserJson user) {
        // todo
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        // todo
        return null;
    }

    @Override
    public void createOutcomeInvitations(UserJson user, UserJson targetUser) {
        // todo
    }

    @Override
    public List<UserJson> createFriends(UserJson targetUser, int count) {
        // todo
        return null;
    }

    @Override
    public void createFriends(UserJson user1, UserJson user2) {
        // todo
    }

    @Override
    public void deleteUser(String username) {
        // todo
    }
}
