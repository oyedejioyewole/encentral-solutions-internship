package controllers;

import com.encentral.image_inverter.impl.ImageProcessingService;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

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
                    .thenApply(Results::ok)
                    .exceptionally(throwable -> notFound("Image not found: " + throwable.getMessage()));
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(
                    badRequest("Invalid UUID format: " + id)
            );
        }
    }

}