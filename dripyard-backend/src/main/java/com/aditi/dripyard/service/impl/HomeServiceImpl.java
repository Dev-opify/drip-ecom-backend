package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.domain.HomeCategorySection;
import com.aditi.dripyard.model.Deal;
import com.aditi.dripyard.model.Home;
import com.aditi.dripyard.model.HomeCategory;
import com.aditi.dripyard.repository.DealRepository;
import com.aditi.dripyard.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HomeServiceImpl implements HomeService {

    private final DealRepository dealRepository;



    @Override
    public Home creatHomePageData(List<HomeCategory> allCategories) {


        List<HomeCategory> gridCategories = allCategories.stream()
                .filter(category ->
                        category.getSection() == HomeCategorySection.GRID)
                .collect(Collectors.toList());

        List<HomeCategory> shopByCategories = allCategories.stream()
                .filter(category ->
                        category.getSection() == HomeCategorySection.SHOP_BY_CATEGORIES)
                .collect(Collectors.toList());

        List<HomeCategory> tshirtCategories = allCategories.stream()
                .filter(category ->
                        category.getSection() == HomeCategorySection.TSHIRT_CATEGORIES)
                .collect(Collectors.toList());

        List<HomeCategory> hoodieCategories = allCategories.stream()
                .filter(category ->
                        category.getSection() == HomeCategorySection.HOODIE_CATEGORIES)
                .collect(Collectors.toList());


        List<HomeCategory> dealCategories = allCategories.stream()
                .filter(category -> category.getSection() == HomeCategorySection.DEALS)
                .toList();

        List<Deal> createdDeals = new ArrayList<>();

        if (dealRepository.findAll().isEmpty()) {
            List<Deal> deals = allCategories.stream()
                    .filter(category -> category.getSection() == HomeCategorySection.DEALS)
                    .map(category -> new Deal(null, 10, category))  // Assuming a discount of 10 for each deal
                    .collect(Collectors.toList());
            createdDeals = dealRepository.saveAll(deals);
        } else createdDeals = dealRepository.findAll();


        Home home = new Home();
        home.setGrid(gridCategories);
        home.setShopByCategories(shopByCategories);
        home.setTshirtCategories(tshirtCategories);
        home.setHoodieCategories(hoodieCategories);
        home.setDeals(dealCategories);



        return home;
    }


}
