package com.aditi.dripyard.model;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;

@Data
public class Home {

    private List<HomeCategory> grid;

    private List<HomeCategory> shopByCategories;

    private List<HomeCategory> tshirtCategories;

    private List<HomeCategory> hoodieCategories;

    private List<Deal> deals;





}
