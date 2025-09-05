package com.aditi.dripyard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Home {

    private List<HomeCategory> grid;

    private List<HomeCategory> shopByCategories;

    private List<HomeCategory> tshirtCategories;

    private List<HomeCategory> hoodieCategories;

    private List<HomeCategory> deals;
}





//    GRID,
//    SHOP_BY_CATEGORIES,
//    TSHIRT_CATEGORIES,
//    HOODIE_CATEGORIES,
//    DEALS