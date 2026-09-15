package com.nguyen.spider;

import java.util.Set;

public record ParseResult(
        Set<String> urlList,
        Set<String> imageList
) {
}
