package util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;

public class PdfExporter {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
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
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'export PDF: " + e.getMessage());
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
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'export PDF: " + e.getMessage());
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
            case "Exploitation":
                return new String[]{"ID", "Nom Exploitation", "Type", "Superficie", "Adresse", "Ville ID", "Agence ID"};
            case "Dirige":
                return new String[]{"ID", "Personne ID", "Fonction", "Agence ID", "Exploitation ID", "Date Début"};
            case "EstComptablePour":
                return new String[]{"ID", "Personne ID", "ID Entité", "Type Entité", "Date Début"};
            case "EstComptableDans":
                return new String[]{"ID", "Personne ID", "Agence ID", "Poste", "Date Début"};
            default:
                return new String[]{"ID", "Valeur"};
        }
    }
    
    private static String[] getEntityValues(Object entity) {
        try {
            String className = entity.getClass().getSimpleName();
            switch (className) {
                case "Personne":
                    Method getIdPersonne = entity.getClass().getMethod("getIdPersonne");
                    Method getNom = entity.getClass().getMethod("getNom");
                    Method getPrenom = entity.getClass().getMethod("getPrenom");
                    Method getDateNaissance = entity.getClass().getMethod("getDateNaissance");
                    Method getTelephone = entity.getClass().getMethod("getTelephone");
                    
                    Object dateNaissance = getDateNaissance.invoke(entity);
                    String dateStr = dateNaissance != null ? dateFormat.format(dateNaissance) : "";
                    
                    return new String[]{
                        String.valueOf(getIdPersonne.invoke(entity)),
                        (String) getNom.invoke(entity),
                        (String) getPrenom.invoke(entity),
                        dateStr,
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
                    
                case "Agence":
                    Method getIdAgence = entity.getClass().getMethod("getIdAgence");
                    Method getNomAgence = entity.getClass().getMethod("getNomAgence");
                    Method getAdresse = entity.getClass().getMethod("getAdresse");
                    Method getTelephoneAgence = entity.getClass().getMethod("getTelephone");
                    Method getEmail = entity.getClass().getMethod("getEmail");
                    Method getIdVilleAgence = entity.getClass().getMethod("getIdVille");
                    
                    return new String[]{
                        String.valueOf(getIdAgence.invoke(entity)),
                        (String) getNomAgence.invoke(entity),
                        (String) getAdresse.invoke(entity),
                        (String) getTelephoneAgence.invoke(entity),
                        (String) getEmail.invoke(entity),
                        String.valueOf(getIdVilleAgence.invoke(entity))
                    };
                    
                case "Exploitation":
                    Method getIdExploitation = entity.getClass().getMethod("getIdExploitation");
                    Method getNomExploitation = entity.getClass().getMethod("getNomExploitation");
                    Method getTypeExploitation = entity.getClass().getMethod("getTypeExploitation");
                    Method getSuperficie = entity.getClass().getMethod("getSuperficie");
                    Method getAdresseExploitation = entity.getClass().getMethod("getAdresse");
                    Method getIdVilleExploitation = entity.getClass().getMethod("getIdVille");
                    Method getIdAgenceExploitation = entity.getClass().getMethod("getIdAgence");
                    
                    return new String[]{
                        String.valueOf(getIdExploitation.invoke(entity)),
                        (String) getNomExploitation.invoke(entity),
                        (String) getTypeExploitation.invoke(entity),
                        String.valueOf(getSuperficie.invoke(entity)),
                        (String) getAdresseExploitation.invoke(entity),
                        String.valueOf(getIdVilleExploitation.invoke(entity)),
                        String.valueOf(getIdAgenceExploitation.invoke(entity))
                    };
                    
                case "Dirige":
                    Method getIdDirige = entity.getClass().getMethod("getIdDirige");
                    Method getPersonneDirige = entity.getClass().getMethod("getPersonne");
                    Method getFonction = entity.getClass().getMethod("getFonction");
                    Method getAgenceDirige = entity.getClass().getMethod("getAgence");
                    Method getExploitationDirige = entity.getClass().getMethod("getExploitation");
                    Method getDateDebutDirige = entity.getClass().getMethod("getDateDebut");
                    
                    Object personne = getPersonneDirige.invoke(entity);
                    Object agence = getAgenceDirige.invoke(entity);
                    Object exploitation = getExploitationDirige.invoke(entity);
                    Object dateDebut = getDateDebutDirige.invoke(entity);
                    
                    String personneId = personne != null ? 
                        String.valueOf(personne.getClass().getMethod("getIdPersonne").invoke(personne)) : "";
                    String agenceId = agence != null ? 
                        String.valueOf(agence.getClass().getMethod("getIdAgence").invoke(agence)) : "";
                    String exploitationId = exploitation != null ? 
                        String.valueOf(exploitation.getClass().getMethod("getIdExploitation").invoke(exploitation)) : "";
                    String dateDebutStr = dateDebut != null ? dateFormat.format(dateDebut) : "";
                    
                    return new String[]{
                        String.valueOf(getIdDirige.invoke(entity)),
                        personneId,
                        (String) getFonction.invoke(entity),
                        agenceId,
                        exploitationId,
                        dateDebutStr
                    };
                    
                // Ajoutez les autres cas pour EstComptablePour et EstComptableDans...
                    
                default:
                    return new String[]{entity.toString()};
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{};
        }
    }
    
    // Méthode spécifique pour Personne (pour compatibilité)
    public static void exportPersonnesToPdf(List<?> personnes, String fileName) {
        exportListToPdf(personnes, fileName);
    }
}