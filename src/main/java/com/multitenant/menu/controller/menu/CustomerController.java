package com.multitenant.menu.controller.menu;

import com.multitenant.menu.api.CategoriesApi;
import com.multitenant.menu.model.Category;
import com.multitenant.menu.model.Product;
import com.multitenant.menu.controller.AbstractController;
import com.multitenant.menu.entity.sql.CategoryEntity;
import com.multitenant.menu.entity.sql.ProductEntity;
import com.multitenant.menu.mapper.CategoryMapper;
import com.multitenant.menu.mapper.ProductMapper;
import com.multitenant.menu.services.CategoryService;
import com.multitenant.menu.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController extends AbstractController implements CategoriesApi {
    private final CustomerService customerService;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<List<Category>> getCategories(String xTenantID) {
        logInfo("Fetching all categories for tenant: " + xTenantID);
        
        List<CategoryEntity> categoryEntities = categoryService.getAllCategories();
        List<Category> categories = categoryEntities.stream()
                .map(categoryMapper::toApiModel)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<List<Product>> getCategoryProducts(String xTenantID, Integer id, Integer branchId, String orderMode) {
        logInfo("Fetching products for category: " + id + " in branch: " + branchId);
        List<ProductEntity> products = categoryService.getProductsByCategory(Long.valueOf(id));
        List<Product> apiProducts = products.stream()
                .map(productMapper::toApiModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(apiProducts);
    }
}
