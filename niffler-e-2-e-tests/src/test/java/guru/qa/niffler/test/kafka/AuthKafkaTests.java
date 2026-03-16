package guru.qa.niffler.test.kafka;

import guru.qa.niffler.api.impl.AuthApiClient;
import guru.qa.niffler.jupiter.annotation.meta.KafkaTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.KafkaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@KafkaTest
public class AuthKafkaTests {

    private final AuthApiClient authApiClient = new AuthApiClient();

   @Test
   void userRegisterTest() throws InterruptedException {
       UserJson userJson = KafkaService.getUser("duck");

       Assertions.assertEquals("duck", userJson.username());
       System.out.println(userJson.id());
   }
}
