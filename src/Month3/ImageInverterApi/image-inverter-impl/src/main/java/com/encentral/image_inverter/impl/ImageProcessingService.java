package com.encentral.image_inverter.impl;

import akka.actor.ActorSystem;
import com.encentral.entities.JpaProcessedImage;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.dispatch.Futures.future;
import static scala.compat.java8.FutureConverters.toJava;

@Singleton
public class ImageProcessingService {

    private static final Logger.ALogger logger = Logger.of(ImageProcessingService.class);
    private static final String UPLOAD_DIR = "uploads/processed";

    private final ActorSystem actorSystem;
    private final ProcessedImageRepository repository;

    @Inject
    public ImageProcessingService(ActorSystem actorSystem, ProcessedImageRepository repository) {
        this.actorSystem = actorSystem;
        this.repository = repository;
        initializeUploadDirectory();
        verifyImageMagickInstallation();
    }

    private void initializeUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", UPLOAD_DIR);
            }
        } catch (IOException e) {
            logger.error("Failed to create upload directory", e);
            throw new RuntimeException("Failed to initialize upload directory", e);
        }
    }

    private void verifyImageMagickInstallation() {
        try {
            ConvertCmd cmd = new ConvertCmd();
            IMOperation op = new IMOperation();
            op.version();
            cmd.run(op);
            logger.info("ImageMagick is installed and available");
        } catch (Exception e) {
            logger.warn("ImageMagick may not be installed or not in PATH: {}", e.getMessage());
            logger.warn("Please install ImageMagick: sudo apt-get install imagemagick (Linux) or brew install imagemagick (Mac)");
        }
    }

    public CompletionStage<JpaProcessedImage> processAndSaveImage(File sourceFile, String originalFileName) {
        return toJava(future(() -> {
            File outputFile = null;
            try {
                // Generate unique filename
                String fileExtension = getFileExtension(originalFileName);
                String processedFileName = UUID.randomUUID().toString() + "." + fileExtension;
                Path outputPath = Paths.get(UPLOAD_DIR, processedFileName);
                outputFile = outputPath.toFile();

                // Use ImageMagick to invert colors
                invertImageWithImageMagick(sourceFile, outputFile);

                // Get file size
                long fileSize = Files.size(outputPath);

                // Create and save entity
                JpaProcessedImage processedImage = JpaProcessedImage.builder()
                        .filePath(outputPath.toString())
                        .originalFileName(originalFileName)
                        .fileSize(fileSize)
                        .build();

                JpaProcessedImage savedImage = repository.save(processedImage);
                logger.info("Successfully processed and saved image: {} using ImageMagick", savedImage.getId());

                return savedImage;
            } catch (Exception e) {
                logger.error("Failed to process image", e);
                // Clean up output file if it was created
                if (outputFile != null && outputFile.exists()) {
                    outputFile.delete();
                }
                throw new RuntimeException("Image processing failed: " + e.getMessage(), e);
            } finally {
                // Clean up temporary source file
                if (sourceFile.exists()) {
                    sourceFile.delete();
                }
            }
        }, actorSystem.dispatcher()));
    }

    private void invertImageWithImageMagick(File inputFile, File outputFile)
            throws IOException, InterruptedException, IM4JavaException {

        ConvertCmd cmd = new ConvertCmd();

        // Optional: Set ImageMagick path if not in system PATH
        // cmd.setSearchPath("/usr/local/bin"); // Adjust as needed

        IMOperation op = new IMOperation();
        op.addImage(inputFile.getAbsolutePath());
        op.negate(); // This inverts the colors
        op.addImage(outputFile.getAbsolutePath());

        logger.debug("Running ImageMagick command: convert {} -negate {}",
                inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

        cmd.run(op);

        logger.debug("ImageMagick processing completed for: {}", outputFile.getName());
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "jpg"; // default extension
    }

    public CompletionStage<File> getImageFile(UUID imageId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findById(imageId)
                    .map(image -> {
                        File file = new File(image.getFilePath());
                        if (!file.exists()) {
                            logger.error("Image file not found for ID: {}", imageId);
                            throw new RuntimeException("Image file not found");
                        }
                        return file;
                    })
                    .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));
        }, actorSystem.dispatcher());
    }
}