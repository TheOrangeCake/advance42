package com.nguyen.scorpion.chain;

import com.nguyen.scorpion.model.ExifTag;
import com.nguyen.scorpion.model.ImageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParserChain extends ChainAbstract {
    private static final Logger logger = LogManager.getLogger(ParserChain.class);

    public void handle(ImageContext context) {
    }

    private Map<ExifTag, String> extractExif(byte[] data) {

    }
}
