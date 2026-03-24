package com.langly.app.student.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class PdfGeneratorService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String generateCertificate(
            String studentName,
            String courseName,
            String language,
            String level,
            String schoolName,
            LocalDateTime issuedAt) {

        String filename = "cert_" + UUID.randomUUID() + ".pdf";
        Path certDir = Paths.get(uploadDir, "certificates").toAbsolutePath().normalize();

        try {
            Files.createDirectories(certDir);
            Path filePath = certDir.resolve(filename);

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                float pageWidth = page.getMediaBox().getWidth();
                float yPos = 700;

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    // Titre
                    String title = "CERTIFICAT DE REUSSITE";
                    float titleWidth = titleFont.getStringWidth(title) / 1000 * 24;
                    content.beginText();
                    content.setFont(titleFont, 24);
                    content.newLineAtOffset((pageWidth - titleWidth) / 2, yPos);
                    content.showText(title);
                    content.endText();
                    yPos -= 60;

                    // Sous-titre
                    String subtitle = schoolName;
                    float subtitleWidth = bodyFont.getStringWidth(subtitle) / 1000 * 14;
                    content.beginText();
                    content.setFont(bodyFont, 14);
                    content.newLineAtOffset((pageWidth - subtitleWidth) / 2, yPos);
                    content.showText(subtitle);
                    content.endText();
                    yPos -= 80;

                    // Corps
                    String[] lines = {
                            "Nous certifions que",
                            "",
                            studentName,
                            "",
                            "a termine avec succes le cours",
                            "",
                            courseName,
                            "",
                            "Langue : " + language + "   |   Niveau : " + level,
                            "",
                            "Delivre le " + issuedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    };

                    for (String line : lines) {
                        if (line.isEmpty()) {
                            yPos -= 15;
                            continue;
                        }
                        PDType1Font font = line.equals(studentName) || line.equals(courseName)
                                ? titleFont
                                : bodyFont;
                        float fontSize = line.equals(studentName) ? 18 : (line.equals(courseName) ? 16 : 12);
                        float lineWidth = font.getStringWidth(line) / 1000 * fontSize;
                        content.beginText();
                        content.setFont(font, fontSize);
                        content.newLineAtOffset((pageWidth - lineWidth) / 2, yPos);
                        content.showText(line);
                        content.endText();
                        yPos -= 30;
                    }
                }

                document.save(filePath.toFile());
            }

            return "certificates/" + filename;
        } catch (IOException e) {
            log.error("Erreur lors de la génération du certificat PDF", e);
            throw new RuntimeException("Impossible de générer le certificat PDF", e);
        }
    }
}
