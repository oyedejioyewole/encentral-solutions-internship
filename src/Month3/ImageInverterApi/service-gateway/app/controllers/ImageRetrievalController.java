package controllers;

import com.encentral.image_inverter.impl.ImageProcessingService;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ImageRetrievalController extends Controller {

    private final ImageProcessingService imageProcessingService;

    @Inject
    public ImageRetrievalController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    public CompletionStage<Result> getImage(String id) {
        try {
            UUID imageId = UUID.fromString(id);

            return imageProcessingService.getImageFile(imageId)
                    .thenApply(file -> {
                        String contentType = getContentType(file.getName());
                        return ok(file).as(contentType);
                    })
                    .exceptionally(throwable -> notFound("Image not found: " + throwable.getMessage()));
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(
                    badRequest("Invalid UUID format: " + id)
            );
        }
    }

    private String getContentType(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCaseFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerCaseFileName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCaseFileName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lowerCaseFileName.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg"; // default
    }
}