package org.example;

import org.example.db.DatabaseManager;
import org.example.export.ExcelExporter;
import org.example.export.PdfExporter;
import org.example.product.Product;
import org.example.product.ProductFactory;
import org.example.product.ShopifyData;
import org.example.utils.JsonParser;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("start");
        DatabaseManager dbManager = new DatabaseManager();
        URL inputFile = Main.class.getClassLoader().getResource("input/response.json");
        Path path = Paths.get(inputFile.toURI());

        ShopifyData shopifyData = JsonParser.parseJson(path.toString());

        if (shopifyData != null) {
            List<Product> products = ProductFactory.createProducts(shopifyData);
            if (!products.isEmpty()) {
                dbManager.insertProducts(products);

                List<Product> allProducts = dbManager.getProducts();

                System.out.println("Do you want to export the data?\n");
                System.out.println("If you want to export it as Excel file, press '1'\n");
                System.out.println("If you want to export it as PDF file, press '2'\n");
                System.out.println("For exit, press '3'\n");

                InputStream inputStream = System.in;
                Reader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String choise = bufferedReader.readLine();
                switch (choise){

                    case "1": ExcelExporter.exportToExcel(allProducts, "products.xlsx");
                    break;

                    case "2": PdfExporter.exportToPdf(allProducts, "products.pdf");
                    break;

                    case "3": System.exit(0);
                    break;

                    default:
                        System.out.println("No such a menu variant");
                        System.exit(0);
                }
            } else {
                System.out.println("No products found in JSON.");
            }
        } else {
            System.out.println("Error: Could not read product data from JSON file.");
        }
    }
}

