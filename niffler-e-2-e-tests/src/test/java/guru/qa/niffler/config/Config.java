package guru.qa.niffler.config;

import java.util.List;

public interface Config {

  static Config getInstance() {
    return LocalConfig.INSTANCE;
  }

  String frontUrl();

  String authUrl();

  String authJdbcUrl();

  String userdataUrl();

  String userdataJdbcUrl();

  String spendUrl();

  String spendJdbcUrl();

  String currencyJdbcUrl();

  String gatewayUrl();

  String ghUrl();

  String kafkaAddress();

  default List<String> kafkaTopics() {
    return List.of("users");
  }
}
