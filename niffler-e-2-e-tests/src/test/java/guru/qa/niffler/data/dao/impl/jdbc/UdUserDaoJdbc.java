package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UdUserDaoJdbc implements UdUserDao {

    Config CFG = Config.getInstance();

    @Override
    public UdUserEntity create(UdUserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.user (username, currency) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.executeUpdate();
            final UUID generatedUserId;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getObject("id", UUID.class);
                } else {
                    throw new IllegalStateException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedUserId);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement("SELECT * FROM public.user WHERE id = ? ")) {
            ps.setObject(1, id);

            ps.execute();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) {
                UdUserEntity result = new UdUserEntity();
                result.setId(rs.getObject("id", UUID.class));
                result.setUsername(rs.getString("username"));
                result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                result.setFirstname(rs.getString("firstname"));
                result.setSurname(rs.getString("surname"));
                result.setPhoto(rs.getBytes("photo"));
                result.setPhotoSmall(rs.getBytes("photo_small"));
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UdUserEntity> findByUsername(String username) {
        try (PreparedStatement statement = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.user WHERE username = ?"
        )) {
            statement.setObject(1, username);
            statement.execute();
            try (ResultSet rs = statement.getResultSet()) {
                if (rs.next()) {
                    UdUserEntity ue = new UdUserEntity();
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UdUserEntity user) {
        try (PreparedStatement statement = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM public.user WHERE id = ?"
        )) {
            statement.setObject(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UdUserEntity> findAll() {
        List<UdUserEntity> users = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement("SELECT * FROM public.user")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UdUserEntity user = new UdUserEntity();
                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    user.setFirstname(rs.getString("firstname"));
                    user.setSurname(rs.getString("surname"));
                    user.setPhoto(rs.getBytes("photo"));
                    user.setPhotoSmall(rs.getBytes("photo_small"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}
