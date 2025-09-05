package com.aditi.dripyard.service;


import com.aditi.dripyard.model.Home;
import com.aditi.dripyard.model.HomeCategory;

import java.util.List;

public interface HomeService {

    Home creatHomePageData(List<HomeCategory> categories);

}
