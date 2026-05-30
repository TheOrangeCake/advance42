package com.nguyen.spider;

import com.nguyen.helper.MyLogger;

public class Spider {
    static void main(String[] av) {
        MyLogger.info("Hello, this is Spider program");

        if (av.length < 1) {
            MyLogger.fatal("Please launch with an URL, with optional options r (recursive), l (max_depth) and p (download_path)");
            return;
        }

        OptionConfig config = new OptionConfig();
        try {
            config.parse(av);
            if (!config.isOption_r()) {
                MyLogger.debug("HEY");
            }
        } catch (OptionParseException e) {
            MyLogger.fatal("Parse Error", e);
        }
    }
}