package com.nguyen.spider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionConfig {
    private boolean option_r = false;
    private String URL;
    private boolean option_l = false;
    private int max_depth = 5;
    private boolean option_p = false;
    private String path = "./data/";

    public OptionConfig() {

    }

    public void parse(String[] av) throws OptionParseException {
        throw new OptionParseException("test");
    }
}
