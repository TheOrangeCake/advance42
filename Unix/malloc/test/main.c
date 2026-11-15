/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   main.c                                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/15 23:25:09 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/16 00:02:07 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"
#include <string.h>
#include <stdio.h>

static bool g_fail = false;

static void check(bool cond, const char *name) {
    char    buf[128];
    int     len;

    if (cond == true) {
        len = snprintf(buf, sizeof(buf), "[OK]   %s\n", name);
    } else {
        len = snprintf(buf, sizeof(buf), "[FAIL] %s\n", name);
        g_fail = true;
    }
    write(1, buf, len);
}

static void test_basic(void) {
    unsigned char   *ptr;
    size_t          size;

    size = 5000;
    ptr = malloc(size);
    check(ptr != NULL, "malloc(5000) is not NULL");
    check((uintptr_t)ptr % 16 == 0, "pointer is 16-aligned");
    memset(ptr, 0xAB, size);
    check(ptr[size - 1] == 0xAB, "full range is writable");
    free(ptr);
}

static void test_unlink(void) {
    void    *a;
    void    *b;
    void    *c;

    a = malloc(5000);
    b = malloc(5000);
    c = malloc(5000);
    free(b);
    free(c);
    free(a);
    check(g_malloc.large == NULL, "list empty after freeing 3 blocks");
    check(g_malloc.total_allocated == 0, "total back to 0");
}

static void test_edge(void) {
    void    *ptr;

    free(NULL);
    check(true, "free(NULL) does not crash");

    ptr = malloc(5000);
    free(ptr);
    free(ptr);
    check(true, "double free does not crash");

    check(malloc(SIZE_MAX) == NULL, "malloc(SIZE_MAX) is NULL");
}

int main(void) {
    test_basic();
    test_unlink();
    test_edge();

    if (g_fail == true) {
        write(1, "SOME TESTS FAILED\n", 18);
        return 1;
    }
    write(1, "ALL TESTS PASSED\n", 17);
    return 0;
}
