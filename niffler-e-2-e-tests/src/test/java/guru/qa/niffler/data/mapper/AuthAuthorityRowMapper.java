package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthAuthorityRowMapper implements RowMapper<AuthorityEntity> {
    public static final AuthAuthorityRowMapper instance = new AuthAuthorityRowMapper();

    private AuthAuthorityRowMapper() {
    }

    @Override
    public @Nonnull AuthorityEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setUser(rs.getObject("user_id", AuthUserEntity.class));
        authority.setAuthority(Authority.valueOf(rs.getString("authority")));
        return authority;
    }
}
