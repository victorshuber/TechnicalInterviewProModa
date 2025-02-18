package org.example.product;

import java.util.ArrayList;
import java.util.List;

public class ProductFactory {
    public static List<Product> createProducts(ShopifyData shopifyData) {
        List<Product> products = new ArrayList<>();
        if (shopifyData != null && shopifyData.getData() != null
                && shopifyData.getData().getProducts() != null) {
            products.addAll(shopifyData.getData().getProducts().getNodes());
        }
        return products;
    }
}
