package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.enums.CurrencyValues;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@SuppressWarnings("resource")
public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    @Override
    public SpendEntity createSpend(SpendEntity spend) {

        if (spend.getCategory().getId() == null) {

            spend.setCategory(findOrCreateCategoryForSpend(spend));
        }

        return spendDao.create(spend);
    }

    @Override
    public SpendEntity updateSpend(SpendEntity spend) {
        try (PreparedStatement spendPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "UPDATE public.spend " +
                        "SET spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ?" +
                        "WHERE id = ?")
        ) {

            if (spend.getCategory().getId() == null) {

                spend.setCategory(findOrCreateCategoryForSpend(spend));
            }

            spendPs.setDate(1, new Date(spend.getSpendDate().getTime()));
            spendPs.setString(2, spend.getCurrency().name());
            spendPs.setDouble(3, spend.getAmount());
            spendPs.setString(4, spend.getDescription());
            spendPs.setObject(5, spend.getCategory().getId());
            spendPs.executeUpdate();

            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name) {
        return categoryDao.findByUsernameAndName(username, name);
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return spendDao.findById(id);
    }

    @Override
    public Optional<SpendEntity> findSpendByUsernameAndDescription(String username, String description) {
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.spend WHERE username = ? AND description = ?"
        )) {
            statement.setString(1, username);
            statement.setString(2, description);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(rs.getObject("currency", CurrencyValues.class));
                    se.setDescription(rs.getString("description"));
                    se.setCategory(rs.getObject("category_id", CategoryEntity.class));
                    return Optional.of(se);
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
    public void removeSpend(SpendEntity spend) {
        spendDao.delete(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDao.delete(category);
    }

    private CategoryEntity findOrCreateCategoryForSpend(SpendEntity spend) {
        Optional<CategoryEntity> existCategory = categoryDao
                .findByUsernameAndName(spend.getUsername(), spend.getCategory().getName());

        return existCategory.orElseGet(() -> categoryDao.create(spend.getCategory()));
    }
}
