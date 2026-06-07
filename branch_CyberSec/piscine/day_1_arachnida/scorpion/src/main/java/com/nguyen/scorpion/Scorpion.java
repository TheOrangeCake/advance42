package com.nguyen.scorpion;

import com.nguyen.scorpion.chain.ChainAbstract;
import com.nguyen.scorpion.chain.ParserChain;
import com.nguyen.scorpion.chain.PrinterChain;
import com.nguyen.scorpion.chain.ValidatorChain;
import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ImageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Scorpion {
    private static final Logger logger = LogManager.getLogger(Scorpion.class);

    static void main(String[] av) {
        logger.info("Hello, this is Scorpion program.");

        if (av.length < 1) {
            logger.fatal("Invalid argument. Retry with at least 1 photo.");
            return;
        }

        ChainAbstract head = new ValidatorChain();
        head.setNext(new ParserChain())
                .setNext(new PrinterChain());

        for (String arg : av) {
            logger.info("Handling: {}", arg);
            Path path = Paths.get(arg);
            ImageContext context = new ImageContext(path);
            try {
                head.handle(context);
            } catch (ScorpionException e) {
                logger.error("Detected problem. Skip", e);
            }
        }
    }
}
