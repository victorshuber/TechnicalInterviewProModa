package org.example.export;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.product.Product;

import java.io.FileOutputStream;
import java.util.List;

public class PdfExporter {
    public static void exportToPdf(List<Product> products, String filePath) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Product List"));
            for (Product p : products) {
                document.add(new Paragraph(p.getId() + " - " + p.getTitle() + " - " + p.getVendor() + " - $" + p.getVariants().getNodes().getFirst().getPrice()));
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
