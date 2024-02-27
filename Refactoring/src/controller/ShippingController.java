package controller;

import service.ShippingServiceImpl;

public class ShippingController {
    public static void main(String[] args) {

        ShippingServiceImpl shippingService = new ShippingServiceImpl();
        shippingService.menuList();
    }
}