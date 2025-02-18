package org.example.export;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.product.Product;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    public static void exportToExcel(List<Product> products, String filePath) {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet("Products");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Title");
            header.createCell(2).setCellValue("Vendor");
            header.createCell(3).setCellValue("Price");
            int rowNum = 1;
            for (Product p : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getTitle());
                row.createCell(2).setCellValue(p.getVendor());
                row.createCell(3).setCellValue(p.getVariants().getNodes().getFirst().getPrice());
            }
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
