package com.nguyen.scorpion.chain;

import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ImageContext;

public interface Chain {
    void handle(ImageContext context) throws ScorpionException;
}
