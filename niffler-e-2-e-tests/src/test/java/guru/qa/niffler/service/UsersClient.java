package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {

    UserJson createUser(String username, String password);

    void createIncomeInvitations(UserJson targetUser, int count);

    void createIncomeInvitations(UserJson userFrom, UserJson user);

    void createOutcomeInvitations(UserJson targetUser, int count);

    void createOutcomeInvitations(UserJson user, UserJson targetUser);

    void createFriends(UserJson targetUser, int count);

    void createFriends(UserJson user1, UserJson user2);

    void deleteUser(String username);
}
