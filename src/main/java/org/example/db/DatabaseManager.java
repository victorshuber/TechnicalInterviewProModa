package org.example.db;

import org.example.product.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mariadb://localhost:3306/shopify";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    public void insertProducts(List<Product> products) {
        try (Connection conn = getConnection()) {
            // Вставляем данные в таблицу products
            String productSql = "INSERT INTO products (id, is_gift_card, published_at, status, title, updated_at, vendor)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)\n" +
                    "ON DUPLICATE KEY UPDATE\n" +
                    "    is_gift_card = VALUES(is_gift_card),\n" +
                    "    published_at = VALUES(published_at),\n" +
                    "    status = VALUES(status),\n" +
                    "    title = VALUES(title),\n" +
                    "    updated_at = VALUES(updated_at),\n" +
                    "    vendor = VALUES(vendor);";
            PreparedStatement productStmt = conn.prepareStatement(productSql);

            // Вставляем данные в таблицу variants
            String variantSql = "INSERT INTO variants (id, barcode, compare_at_price, price, sku, title, product_id, inventory_item_id)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)\n" +
                    "ON DUPLICATE KEY UPDATE\n" +
                    "    barcode = VALUES(barcode),\n" +
                    "    compare_at_price = VALUES(compare_at_price),\n" +
                    "    price = VALUES(price),\n" +
                    "    sku = VALUES(sku),\n" +
                    "    title = VALUES(title),\n" +
                    "    product_id = VALUES(product_id),\n" +
                    "    inventory_item_id = VALUES(inventory_item_id);";


            PreparedStatement variantStmt = conn.prepareStatement(variantSql);

            // Вставляем данные в таблицу inventory_items
            String inventorySql = "INSERT INTO inventory_items (id, sku) VALUES (?, ?) ON DUPLICATE KEY UPDATE sku = VALUES(sku);";
            PreparedStatement inventoryStmt = conn.prepareStatement(inventorySql);

            for (Product p : products) {
                // Извлекаем ID продукта
                String[] idParts = p.getId().split("/");
                long productId = Long.parseLong(idParts[idParts.length - 1]);

                // Вставляем данные в таблицу products
                productStmt.setLong(1, productId);
                productStmt.setBoolean(2, p.isGiftCard());
                productStmt.setString(3, p.getPublishedAt());
                productStmt.setString(4, p.getStatus());
                productStmt.setString(5, p.getTitle());
                productStmt.setString(6, p.getUpdatedAt());
                productStmt.setString(7, p.getVendor());
                productStmt.executeUpdate();

                // Вставляем варианты (variants)
                for (Node variant : p.getVariants().getNodes()) {
                    String[] variantIdParts = variant.getId().split("/");
                    long variantId = Long.parseLong(variantIdParts[variantIdParts.length - 1]);

                    String[] inventoryIdParts = variant.getInventoryItem().getId().split("/");
                    long inventoryItemId = Long.parseLong(inventoryIdParts[inventoryIdParts.length - 1]);

                    // Вставляем данные в таблицу variants
                    variantStmt.setLong(1, variantId);
                    variantStmt.setString(2, variant.getBarcode());
                    variantStmt.setString(3, variant.getCompareAtPrice());
                    variantStmt.setDouble(4, Double.parseDouble(variant.getPrice()));
                    variantStmt.setString(5, variant.getSku());
                    variantStmt.setString(6, variant.getTitle());
                    variantStmt.setLong(7, productId); // Связь с продуктом

                    // Вставляем данные в таблицу inventory_items
                    inventoryStmt.setLong(1, inventoryItemId);
                    inventoryStmt.setString(2, variant.getInventoryItem().getSku());
                    inventoryStmt.executeUpdate();

                    variantStmt.setLong(8, inventoryItemId); // Связь с inventory_item
                    variantStmt.executeUpdate();


                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();

        try (Connection conn = getConnection()) {
            // Запрос для получения продуктов
            String productSql = "SELECT id, is_gift_card, published_at, status, title, updated_at, vendor FROM products";
            PreparedStatement productStmt = conn.prepareStatement(productSql);
            ResultSet productRs = productStmt.executeQuery();

            while (productRs.next()) {
                Product product = new Product();
                product.setId(productRs.getString("id"));
                product.setGiftCard(productRs.getBoolean("is_gift_card"));
                product.setPublishedAt(productRs.getString("published_at"));
                product.setStatus(productRs.getString("status"));
                product.setTitle(productRs.getString("title"));
                product.setUpdatedAt(productRs.getString("updated_at"));
                product.setVendor(productRs.getString("vendor"));

                // Запрос для получения вариантов продукта
                String variantSql = "SELECT id, barcode, compare_at_price, price, sku, title, inventory_item_id FROM variants WHERE product_id = ?";
                PreparedStatement variantStmt = conn.prepareStatement(variantSql);
                variantStmt.setString(1, product.getId());
                ResultSet variantRs = variantStmt.executeQuery();

                List<Node> nodes = new ArrayList<>();
                while (variantRs.next()) {
                    Node node = new Node();
                    node.setId(variantRs.getString("id"));
                    node.setBarcode(variantRs.getString("barcode"));
                    node.setCompareAtPrice(variantRs.getString("compare_at_price"));
                    node.setPrice(variantRs.getString("price"));
                    node.setSku(variantRs.getString("sku"));
                    node.setTitle(variantRs.getString("title"));

                    // Запрос для получения информации о товарном запасе
                    String inventorySql = "SELECT id, sku FROM inventory_items WHERE id = ?";
                    PreparedStatement inventoryStmt = conn.prepareStatement(inventorySql);
                    inventoryStmt.setString(1, variantRs.getString("inventory_item_id"));
                    ResultSet inventoryRs = inventoryStmt.executeQuery();

                    if (inventoryRs.next()) {
                        InventoryItem inventoryItem = new InventoryItem();
                        inventoryItem.setId(inventoryRs.getString("id"));
                        inventoryItem.setSku(inventoryRs.getString("sku"));
                        node.setInventoryItem(inventoryItem);
                    }

                    nodes.add(node);
                }

                Variant variants = new Variant();
                variants.setNodes(nodes);
                product.setVariants(variants);

                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
}


