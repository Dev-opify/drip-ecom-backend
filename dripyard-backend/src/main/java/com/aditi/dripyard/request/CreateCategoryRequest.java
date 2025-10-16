package com.aditi.dripyard.request;

import lombok.Data;

@Data
public class CreateCategoryRequest {

    private String parentCategoryId;
    private int level;
    private String name;
    private String categoryId;
    private CreateCategoryRequest parentCategory;
}
