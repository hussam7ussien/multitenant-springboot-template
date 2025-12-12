package com.multitenant.menu.services;

import com.multitenant.menu.entity.sql.CategoryEntity;
import com.multitenant.menu.entity.sql.ProductEntity;
import com.multitenant.menu.entity.sql.ProductOptionEntity;
import com.multitenant.menu.repository.sql.CategoryRepository;
import com.multitenant.menu.repository.sql.ProductRepository;
import com.multitenant.menu.repository.ProductOptionRepository;
import com.multitenant.menu.repository.OptionChoiceRepository;
import com.multitenant.menu.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final OptionChoiceRepository optionChoiceRepository;

    /**
     * Fetch all categories for the current tenant from their respective database
     */
    public List<CategoryEntity> getAllCategories() {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching all categories for tenant: {}", tenantId);
        return categoryRepository.findAll();
    }

    /**
     * Fetch a category by ID for the current tenant
     */
    public Optional<CategoryEntity> getCategoryById(Long id) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching category {} for tenant: {}", id, tenantId);
        return categoryRepository.findById(id);
    }

    /**
     * Create a new category for the current tenant
     */
    public CategoryEntity createCategory(CategoryEntity category) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Creating category for tenant: {}", tenantId);
        return categoryRepository.save(category);
    }

    /**
     * Update an existing category for the current tenant
     */
    public CategoryEntity updateCategory(Long id, CategoryEntity categoryDetails) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Updating category {} for tenant: {}", id, tenantId);
        
        Optional<CategoryEntity> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            CategoryEntity category = existingCategory.get();
            if (categoryDetails.getName() != null) {
                category.setName(categoryDetails.getName());
            }
            if (categoryDetails.getDescription() != null) {
                category.setDescription(categoryDetails.getDescription());
            }
            if (categoryDetails.getImage() != null) {
                category.setImage(categoryDetails.getImage());
            }
            return categoryRepository.save(category);
        }
        throw new IllegalArgumentException("Category not found with id: " + id);
    }

    /**
     * Delete a category for the current tenant
     */
    public void deleteCategory(Long id) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Deleting category {} for tenant: {}", id, tenantId);
        categoryRepository.deleteById(id);
    }

    /**
     * Fetch all categories with their products for the current tenant
     */
    public List<CategoryEntity> getAllCategoriesWithProducts() {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching all categories with products for tenant: {}", tenantId);
        List<CategoryEntity> categories = categoryRepository.findAll();
        categories.forEach(category -> {
            category.setProducts(productRepository.findByCategoryId(category.getId()));
        });
        return categories;
    }

    /**
     * Fetch products by category id for the current tenant
     */
    public List<ProductEntity> getProductsByCategory(Long categoryId) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching products for category {} for tenant: {}", categoryId, tenantId);
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Fetch a product with all its options and choices
     */
    public ProductEntity getProductWithOptions(Long productId) {
        String tenantId = TenantContext.getTenant().getTenantId();
        log.info("Fetching product {} with options for tenant: {}", productId, tenantId);
        
        Optional<ProductEntity> product = productRepository.findById(productId);
        if (product.isPresent()) {
            ProductEntity p = product.get();
            List<ProductOptionEntity> options = productOptionRepository.findByProductId(productId);
            
            // Load choices for each option
            options.forEach(option -> {
                option.setChoices(optionChoiceRepository.findByOptionId(option.getId()));
            });
            
            p.setOptions(options);
            return p;
        }
        throw new IllegalArgumentException("Product not found with id: " + productId);
    }
}


