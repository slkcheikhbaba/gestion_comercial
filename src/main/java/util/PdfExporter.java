package util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.List;

public class PdfExporter {
    
    public static void exportListToPdf(List<?> entities, String fileName) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        
        try {
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Titre
            String entityName = entities.get(0).getClass().getSimpleName();
            document.add(new Paragraph("Export: " + entityName)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16));
            
            document.add(new Paragraph("\n"));
            
            // Créer un tableau
            Object firstEntity = entities.get(0);
            String[] headers = getHeaders(firstEntity);
            
            Table table = new Table(headers.length);
            
            // En-têtes
            for (String header : headers) {
                table.addHeaderCell(new Paragraph(header).setBold());
            }
            
            // Données
            for (Object entity : entities) {
                String[] values = getEntityValues(entity);
                for (String value : values) {
                    table.addCell(new Paragraph(value != null ? value : ""));
                }
            }
            
            document.add(table);
            document.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void exportEntityToPdf(Object entity, String fileName) {
        try {
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            String entityName = entity.getClass().getSimpleName();
            document.add(new Paragraph("Fiche: " + entityName)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16));
            
            document.add(new Paragraph("\n"));
            
            String[] headers = getHeaders(entity);
            String[] values = getEntityValues(entity);
            
            for (int i = 0; i < headers.length; i++) {
                document.add(new Paragraph(headers[i] + ": " + (values[i] != null ? values[i] : "")));
            }
            
            document.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static String[] getHeaders(Object entity) {
        String className = entity.getClass().getSimpleName();
        switch (className) {
            case "Personne":
                return new String[]{"ID", "Nom", "Prénom", "Date Naissance", "Téléphone"};
            case "Ville":
                return new String[]{"ID", "Nom Ville", "Code Postal"};
            case "Agence":
                return new String[]{"ID", "Nom Agence", "Adresse", "Téléphone", "Email", "Ville ID"};
            default:
                return new String[]{"ID", "Nom"};
        }
    }
    
    private static String[] getEntityValues(Object entity) {
        try {
            String className = entity.getClass().getSimpleName();
            switch (className) {
                case "Personne":
                    Method getId = entity.getClass().getMethod("getIdPersonne");
                    Method getNom = entity.getClass().getMethod("getNom");
                    Method getPrenom = entity.getClass().getMethod("getPrenom");
                    Method getDateNaissance = entity.getClass().getMethod("getDateNaissance");
                    Method getTelephone = entity.getClass().getMethod("getTelephone");
                    
                    return new String[]{
                        String.valueOf(getId.invoke(entity)),
                        (String) getNom.invoke(entity),
                        (String) getPrenom.invoke(entity),
                        String.valueOf(getDateNaissance.invoke(entity)),
                        (String) getTelephone.invoke(entity)
                    };
                case "Ville":
                    Method getIdVille = entity.getClass().getMethod("getIdVille");
                    Method getNomVille = entity.getClass().getMethod("getNomVille");
                    Method getCodePostal = entity.getClass().getMethod("getCodePostal");
                    
                    return new String[]{
                        String.valueOf(getIdVille.invoke(entity)),
                        (String) getNomVille.invoke(entity),
                        (String) getCodePostal.invoke(entity)
                    };
                default:
                    return new String[]{entity.toString()};
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{};
        }
    }
}