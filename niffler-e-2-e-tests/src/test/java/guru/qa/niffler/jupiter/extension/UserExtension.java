package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String defaultPassword = "12345";

    private final UsersClient usersClient = new UsersDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.genRandomUsername();

                        UserJson testUser = usersClient.createUser(username, defaultPassword);

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                testUser.addTestData(
                                        new TestData(
                                                defaultPassword,
                                                new ArrayList<>(),
                                                new ArrayList<>(),
                                                new ArrayList<>(),
                                                new ArrayList<>(),
                                                new ArrayList<>()
                                        )
                                )
                        );
                    }

                    UserJson userJson = context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);

                    UserJson user = userJson == null
                            ? usersClient.findByUsername(userAnno.username()).orElseThrow()
                            : userJson;

                    List<UserJson> incomeInvitation = usersClient.createIncomeInvitations(user, userAnno.incomeInvitations());
                    user.testData().income().addAll(incomeInvitation.stream().map(UserJson::username).toList());

                    List<UserJson> outcomeInvitation = usersClient.createOutcomeInvitations(user, userAnno.outcomeInvitations());
                    user.testData().outcome().addAll(outcomeInvitation.stream().map(UserJson::username).toList());

                    List<UserJson> addedFriends = usersClient.createFriends(user, userAnno.addedFriends());
                    user.testData().addedFriends().addAll(addedFriends.stream().map(UserJson::username).toList());

                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
    }
}
