package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(AuthorityEntity... authority) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO public.authority (user_id, authority) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (AuthorityEntity a : authority) {
                ps.setObject(1, a.getUser().getId());
                ps.setString(2, a.getAuthority().name());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM public.authority WHERE id = ?"
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
                "SELECT * FROM public.authority WHERE user_id = ?"
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
    public void delete(AuthorityEntity... authority) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM public.authority WHERE id = ?"
        )) {
            for (AuthorityEntity a : authority) {
                ps.setObject(1, a.getUser().getId());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
