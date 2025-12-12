package com.multitenant.menu.controller.restaurant;

import com.multitenant.menu.api.ProductsApi;
import com.multitenant.menu.model.Product;
import com.multitenant.menu.controller.AbstractController;
import com.multitenant.menu.entity.sql.ProductEntity;
import com.multitenant.menu.mapper.ProductMapper;
import com.multitenant.menu.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductsController extends AbstractController implements ProductsApi {

    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<Product> getProductDetails(String xTenantID, Integer id) {
        logInfo("Fetching product details for product ID: " + id);
        
        ProductEntity productEntity = categoryService.getProductWithOptions(Long.valueOf(id));
        Product apiProduct = productMapper.toApiModel(productEntity);
        
        return ResponseEntity.ok(apiProduct);
    }
}
