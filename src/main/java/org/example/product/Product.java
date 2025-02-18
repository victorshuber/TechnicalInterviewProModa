package org.example.product;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Product {

    private String id;
    private boolean isGiftCard;
    private String publishedAt;
    private String status;
    private List<String> tags;
    private String title;
    private String updatedAt;
    private Variant variants;
    private String vendor;
}
