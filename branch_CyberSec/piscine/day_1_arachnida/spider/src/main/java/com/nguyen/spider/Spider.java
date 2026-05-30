package com.nguyen.spider;

import com.nguyen.helper.MyLogger;

public class Spider {
    static void main(String[] av) {
        MyLogger.info("Hello, this is Spider program");

        OptionParser optionParser = new OptionParser();
        try {
            optionParser.parse(av);
        } catch (OptionParseException e) {
            MyLogger.fatal("Parse Error", e);
        }
    }
}