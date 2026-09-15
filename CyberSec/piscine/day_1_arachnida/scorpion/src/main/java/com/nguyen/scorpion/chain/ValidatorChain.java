package com.nguyen.scorpion.chain;

import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ImageContext;

import java.nio.file.Files;
import java.nio.file.Path;

public class ValidatorChain extends ChainAbstract {

    public void handle(ImageContext context) {
        Path file = context.getPath();

        if (!Files.exists(file)) {
            throw new ScorpionException("File does not exist.");
        }

        if (!isImage(file)) {
            throw new ScorpionException("File is not a supported image format.");
        }

        context.setExtension(extractExtension(file));

        if (!Files.isReadable(file)) {
            throw new ScorpionException("File is not readable.");
        }

        if (this.next != null) {
            next.handle(context);
        }
    }

    private boolean isImage(Path file) {
        if (file.getFileName() == null) {
            return false;
        }
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".png") || name.endsWith(".gif")
                || name.endsWith(".bmp");
    }

    private String extractExtension(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        int pointPos = name.lastIndexOf(".");
        return name.substring(pointPos + 1);
    }
}
