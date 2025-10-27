package controllers;

import com.encentral.image_inverter.impl.ImageProcessingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ImageUploadController extends Controller {

    private final ImageProcessingService imageProcessingService;

    @Inject
    public ImageUploadController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    public CompletionStage<Result> uploadImage() {
        Http.Request request = request(); // get the current Http.Request
        Http.MultipartFormData<File> body = request.body().asMultipartFormData();

        if (body == null) {
            return CompletableFuture.completedFuture(
                    badRequest(createErrorResponse("No multipart data found"))
            );
        }

        Http.MultipartFormData.FilePart<File> filePart = body.getFile("image");

        if (filePart == null) {
            return CompletableFuture.completedFuture(
                    badRequest(createErrorResponse("No file uploaded with key 'image'"))
            );
        }

        File file = filePart.getFile();
        String fileName = filePart.getFilename();
        String contentType = filePart.getContentType();

        // Validate content type
        if (contentType == null || !contentType.startsWith("image/")) {
            return CompletableFuture.completedFuture(
                    badRequest(createErrorResponse("File must be an image"))
            );
        }

        return imageProcessingService.processAndSaveImage(file, fileName)
                .thenApply(processedImage -> {
                    ObjectNode response = Json.newObject();
                    response.put("id", processedImage.getId().toString());
                    response.put("originalFileName", processedImage.getOriginalFileName());
                    response.put("fileSize", processedImage.getFileSize());
                    response.put("createdAt", processedImage.getCreatedAt().toString());
                    response.put("message", "Image processed successfully");
                    return created(response);
                })
                .exceptionally(throwable -> internalServerError(
                        createErrorResponse("Failed to process image: " + throwable.getMessage())
                ));
    }

    private JsonNode createErrorResponse(String message) {
        ObjectNode error = Json.newObject();
        error.put("error", message);
        return error;
    }
}