package com.langly.app.shared.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * US06 : Service de stockage fichiers sur le disque local.
 */
@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le répertoire d'upload : " + uploadDir, e);
        }
    }

    /**
     * Sauvegarde un fichier et retourne son nom unique.
     */
    public String store(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String uniqueName = UUID.randomUUID() + extension;

        try {
            Path targetPath = rootLocation.resolve(uniqueName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return uniqueName;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du stockage du fichier : " + originalName, e);
        }
    }

    /**
     * Charge un fichier comme Resource.
     */
    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("Fichier introuvable : " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Fichier introuvable : " + filename, e);
        }
    }
}
