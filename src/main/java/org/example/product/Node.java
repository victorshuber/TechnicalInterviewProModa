package org.example.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
public class Node {
    private String barcode;
    private String compareAtPrice;
    private String id;
    private InventoryItem inventoryItem;
    private String price;
    private String sku;
    private String title;
}
