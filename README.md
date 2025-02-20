# TechnicalInterviewProModa
 Special for ProModa

________________________________________
Task description:


Create a console app with JAVA that reads the data from “response.json” (see attachments) and writes it to a MariaDB database.
The data in “response.json” are Shopify products. The exact schema of the data can be found here: https://shopify.dev/docs/api/admin-graphql/2025-01/objects/Product#fields-and-connections.
The definition of the database structure that you can use is described in "db.png" (see attachments).

Extend your console app with an action that allows the user to export the data as...
an Excel file (xlsx)
You can use the Apache POI library (https://mvnrepository.com/artifact/org.apache.poi/poi-scratchpad) for this.
How you structure the data in this format is up to you.
a PDF file
You can use iText v.5 (https://mvnrepository.com/artifact/com.itextpdf/itextpdf) or Apache PDF box (https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox) for this.
How you visualize the data in the PDF file is up to you.
