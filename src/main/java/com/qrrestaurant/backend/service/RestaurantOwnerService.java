package com.qrrestaurant.backend.service;

import com.qrrestaurant.backend.dto.Response.RestaurantOwnerAuthResponse;
import com.qrrestaurant.backend.dto.request.RestaurantOwnerLoginRequest;


public interface RestaurantOwnerService {
    
	RestaurantOwnerAuthResponse login(RestaurantOwnerLoginRequest request);
    
	
    
   
}