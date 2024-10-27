package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.List;

public interface UsersClient {

    UserJson createUser(String username, String password);

    List<UserJson> createIncomeInvitations(UserJson targetUser, int count);

    void createIncomeInvitations(UserJson userFrom, UserJson targetUser);

    List<UserJson> createOutcomeInvitations(UserJson targetUser, int count);

    void createOutcomeInvitations(UserJson user, UserJson targetUser);

    List<UserJson> createFriends(UserJson targetUser, int count);

    void createFriends(UserJson user1, UserJson user2);
}
