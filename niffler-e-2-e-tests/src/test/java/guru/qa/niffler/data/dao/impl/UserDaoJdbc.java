package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserDaoJdbc implements UserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO user (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, user.getUsername());
                statement.setObject(2, user.getCurrency());
                statement.setString(3, user.getFirstname());
                statement.setString(4, user.getSurname());
                statement.setBytes(5, user.getPhoto());
                statement.setBytes(6, user.getPhotoSmall());
                statement.setString(7, user.getFullname());
                statement.executeUpdate();
                final UUID generatedKey;
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Cant find id in ResultSet");
                    }
                }
                user.setId(generatedKey);
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM user WHERE id = ?"
            )) {
                statement.setObject(1, id);
                statement.execute();
                try (ResultSet rs = statement.getResultSet()) {
                    if (rs.next()) {
                        UserEntity ue = new UserEntity();
                        ue.setId(rs.getObject("id", UUID.class));
                        ue.setUsername(rs.getString("username"));
                        ue.setCurrency(rs.getObject("currency", CurrencyValues.class));
                        ue.setFirstname(rs.getString("firstname"));
                        ue.setSurname(rs.getString("surname"));
                        ue.setPhoto(rs.getBytes("photo"));
                        ue.setPhotoSmall(rs.getBytes("photo_small"));
                        ue.setFullname(rs.getString("full_name"));
                        return Optional.of(ue);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM user WHERE username = ?"
            )) {
                statement.setObject(1, username);
                statement.execute();
                try (ResultSet rs = statement.getResultSet()) {
                    if (rs.next()) {
                        UserEntity ue = new UserEntity();
                        ue.setId(rs.getObject("id", UUID.class));
                        ue.setUsername(rs.getString("username"));
                        ue.setCurrency(rs.getObject("currency", CurrencyValues.class));
                        ue.setFirstname(rs.getString("firstname"));
                        ue.setSurname(rs.getString("surname"));
                        ue.setPhoto(rs.getBytes("photo"));
                        ue.setPhotoSmall(rs.getBytes("photo_small"));
                        ue.setFullname(rs.getString("full_name"));
                        return Optional.of(ue);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl());
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM user WHERE id = ?"
             )) {
            statement.setObject(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
