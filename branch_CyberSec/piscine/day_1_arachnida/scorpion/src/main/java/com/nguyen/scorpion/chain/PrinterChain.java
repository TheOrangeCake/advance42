package com.nguyen.scorpion.chain;

import com.nguyen.scorpion.model.ExifTag;
import com.nguyen.scorpion.model.ImageContext;

import java.util.List;
import java.util.Map;

public class PrinterChain extends ChainAbstract {
    public void handle(ImageContext context) {
        System.out.println("==================================================");
        System.out.println("File: " + context.getPath());

        printBasicAttributes(context);
        printMetadata(context);

        System.out.println("==================================================");

        if (this.next != null) {
            next.handle(context);
        }
    }

    private void printBasicAttributes(ImageContext context) {
        Map<String, String> attributes = context.getBasicAttributes();
        if (attributes == null || attributes.isEmpty()) {
            return;
        }
        System.out.println("\n[Basic Attributes]");
        attributes.forEach((key, value) -> System.out.printf("  %-24s : %s%n", key, value));
    }

    private void printMetadata(ImageContext context) {
        Map<ExifTag, String> metadata = context.getMetadata();
        if (metadata == null || metadata.isEmpty()) {
            System.out.println("\nNo EXIF metadata found.");
            return;
        }

        for (ExifTag.Category category : ExifTag.Category.values()) {
            List<Map.Entry<ExifTag, String>> tags = metadata.entrySet().stream()
                    .filter(e -> e.getKey().getCategory() == category)
                    .toList();

            if (tags.isEmpty()) {
                continue;
            }

            System.out.println("\n[" + category.getLabel() + "]");
            for (Map.Entry<ExifTag, String> entry : tags) {
                System.out.printf("  %-24s : %s%n", entry.getKey().getName(), entry.getValue());
            }
        }
    }
}
