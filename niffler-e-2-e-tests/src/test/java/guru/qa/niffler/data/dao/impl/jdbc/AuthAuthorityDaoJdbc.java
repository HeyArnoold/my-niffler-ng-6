package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                ps.setObject(1, a.getUserId());
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
    public List<AuthorityEntity> findAll() {
        List<AuthorityEntity> authorities = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM public.authority");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AuthorityEntity authority = new AuthorityEntity();
                authority.setUserId(rs.getObject("user_id", UUID.class));
                authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                authorities.add(authority);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }
}
