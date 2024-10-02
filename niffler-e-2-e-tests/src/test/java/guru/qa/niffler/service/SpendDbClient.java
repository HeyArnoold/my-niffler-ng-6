package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        return transaction(connection -> {
                    if (spendEntity.getCategory().getId() == null) {
                        Optional<CategoryEntity> existCategory = new CategoryDaoJdbc(connection)
                                .findByUsernameAndName(spend.username(), spend.category().name());
                        if (existCategory.isPresent()) {
                            spendEntity.setCategory(existCategory.get());
                        } else {
                            CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                                    .create(spendEntity.getCategory());
                            spendEntity.setCategory(categoryEntity);
                        }
                    }
                    return SpendJson.fromEntity(new SpendDaoJdbc(connection).create(spendEntity));
                },
                CFG.spendJdbcUrl()
        );
    }

    public void deleteSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        transaction(connection -> {
            new SpendDaoJdbc(connection).delete(spendEntity);
        }, CFG.spendJdbcUrl());
    }

    public CategoryJson createCategory(CategoryJson spend) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(spend);
        return CategoryJson.fromEntity(
                transaction(connection -> {
                    return new CategoryDaoJdbc(connection).create(categoryEntity);
                }, CFG.spendJdbcUrl())
        );
    }

    public void deleteCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        transaction(connection -> {
            new CategoryDaoJdbc(connection).delete(categoryEntity);
        }, CFG.spendJdbcUrl());
    }
}
