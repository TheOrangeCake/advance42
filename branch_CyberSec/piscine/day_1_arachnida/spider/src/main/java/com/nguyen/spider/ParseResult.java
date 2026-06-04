package com.nguyen.spider;

import java.util.List;

public record ParseResult(
        List<String> urlList,
        List<String> imageList
) {
}
