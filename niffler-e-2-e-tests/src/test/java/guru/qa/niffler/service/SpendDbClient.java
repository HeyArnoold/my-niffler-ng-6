package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);

        if (spendEntity.getCategory().getId() == null) {
            Optional<CategoryEntity> existCategory = categoryDao.findByUsernameAndName(spend.username(), spend.category().name());
            if (existCategory.isPresent()) {
                spendEntity.setCategory(existCategory.get());
            } else {
                CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
        }
        return SpendJson.fromEntity(spendDao.create(spendEntity));
    }

    public void deleteSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        spendDao.delete(spendEntity);
    }

    public CategoryJson createCategory(CategoryJson spend) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(spend);
        return CategoryJson.fromEntity(
                categoryDao.create(categoryEntity)
        );
    }

    public void deleteCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        categoryDao.delete(categoryEntity);
    }
}
