package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthorityEntity create(AuthorityEntity authority) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                        "VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setObject(1, authority.getUser().getId());
            statement.setString(2, authority.getAuthority().name());
            statement.executeUpdate();
            final UUID generatedKey;
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Cant find id in ResultSet");
                }
            }
            authority.setId(generatedKey);
            return authority;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM authority WHERE id = ?"
        )) {
            statement.setObject(1, id);
            statement.execute();
            try (ResultSet rs = statement.getResultSet()) {
                if (rs.next()) {
                    AuthorityEntity ue = new AuthorityEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUser(rs.getObject("user_id", AuthUserEntity.class));
                    ue.setAuthority(rs.getObject("authority", Authority.class));
                    return Optional.of(ue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthorityEntity> findByUserId(UUID userId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM authority WHERE user_id = ?"
        )) {
            statement.setObject(1, userId);
            statement.execute();
            try (ResultSet rs = statement.getResultSet()) {
                if (rs.next()) {
                    AuthorityEntity ue = new AuthorityEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUser(rs.getObject("user_id", AuthUserEntity.class));
                    ue.setAuthority(rs.getObject("authority", Authority.class));
                    return Optional.of(ue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthorityEntity authority) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM authority WHERE id = ?"
        )) {
            statement.setObject(1, authority.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
