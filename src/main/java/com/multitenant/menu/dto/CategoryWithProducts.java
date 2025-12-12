package com.multitenant.menu.dto;

import com.multitenant.menu.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithProducts {
    private Long id;
    private String name;
    private String description;
    private String image;
    private List<Product> products;
}
