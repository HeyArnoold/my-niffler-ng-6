package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.data.repository.UdUserRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;
import static guru.qa.niffler.data.tpl.Connections.holder;

@SuppressWarnings("resource")
public class UdUserRepositoryJdbc implements UdUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public UdUserEntity create(UdUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.user (username, currency, firstname, surname, photo, photo_small, full_name) " +
                        "VALUES (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getCurrency().name());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setBytes(5, user.getPhoto());
            userPs.setBytes(6, user.getPhotoSmall());
            userPs.setString(7, user.getFullname());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection()
                .prepareStatement("SELECT * FROM public.user WHERE id = ? ")) {
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
    public void addInvitation(UdUserEntity requester, UdUserEntity addressee) {
        try (PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)")) {

            friendshipPs.setObject(1, requester.getId());
            friendshipPs.setObject(2, addressee.getId());
            friendshipPs.setString(3, PENDING.name());
            friendshipPs.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UdUserEntity requester, UdUserEntity addressee) {
        try (PreparedStatement requesterPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.friendship (requester_id, addressee_id, status) " +
                        "VALUES (?,?,?)");
             PreparedStatement addresseePs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO public.friendship (requester_id, addressee_id, status) VALUES (?,?,?)")) {

            requesterPs.setObject(1, requester.getId());
            requesterPs.setObject(2, addressee.getId());
            requesterPs.setString(3, ACCEPTED.name());
            requesterPs.executeUpdate();

            addresseePs.setObject(1, addressee.getId());
            addresseePs.setObject(2, requester.getId());
            addresseePs.setString(3, ACCEPTED.name());
            addresseePs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
