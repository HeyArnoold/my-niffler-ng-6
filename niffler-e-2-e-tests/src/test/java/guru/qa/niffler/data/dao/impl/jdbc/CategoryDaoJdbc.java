package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class CategoryDaoJdbc implements CategoryDao {

    Config CFG = Config.getInstance();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.category (username, name, archived) " +
                        "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            category.setId(generatedKey);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.category WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("id", UUID.class));
                    ce.setUsername(rs.getString("username"));
                    ce.setName(rs.getString("name"));
                    ce.setArchived(rs.getBoolean("archived"));
                    return Optional.of(ce);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findByUsernameAndName(String username, String categoryName) {
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.category WHERE username = ? AND name = ?"
        )) {
            statement.setString(1, username);
            statement.setString(2, categoryName);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("id", UUID.class));
                    ce.setName(rs.getString("name"));
                    ce.setUsername(rs.getString("username"));
                    ce.setArchived(rs.getBoolean("archived"));
                    return Optional.of(ce);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        List<CategoryEntity> categories = new ArrayList<>();
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.category WHERE username = ?"
        )) {
            statement.setObject(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("id", UUID.class));
                    ce.setName(rs.getString("name"));
                    ce.setUsername(rs.getString("username"));
                    ce.setArchived(rs.getBoolean("archived"));
                    categories.add(ce);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public void delete(CategoryEntity category) {
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM public.category WHERE id = ?"
        )) {
            statement.setObject(1, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAll() {
        List<CategoryEntity> categories = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement("SELECT * FROM public.category");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CategoryEntity category = new CategoryEntity();
                category.setId(rs.getObject("id", UUID.class));
                category.setUsername(rs.getString("username"));
                category.setName(rs.getString("name"));
                category.setArchived(rs.getBoolean("archived"));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }
}
