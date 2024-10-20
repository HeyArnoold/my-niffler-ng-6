package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersClient {

    Optional<UserJson> findById(UUID id);

    Optional<UserJson> findByUsername(String username);

    UserJson createUser(String username, String password);

    List<UserJson> createIncomeInvitations(UserJson targetUser, int count);

    void createIncomeInvitations(UserJson userFrom, UserJson user);

    List<UserJson> createOutcomeInvitations(UserJson targetUser, int count);

    void createOutcomeInvitations(UserJson user, UserJson targetUser);

    List<UserJson> createFriends(UserJson targetUser, int count);

    void createFriends(UserJson user1, UserJson user2);

    void deleteUser(String username);
}
