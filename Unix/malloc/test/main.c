/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   main.c                                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/15 23:25:09 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/21 08:33:46 by hoannguy         ###   ########.fr       */
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
    // test_basic();
    // test_unlink();
    // test_edge();

    // if (g_fail == true) {
    //     write(1, "SOME TESTS FAILED\n", 18);
    //     return 1;
    // }
    // write(1, "ALL TESTS PASSED\n", 17);
    void    *ptr1[110];
    void    *ptr2[50];
    void    *ptr3[10];
    void    *ptr4[10];

    // for (int i = 0; i < 110; i++) {
    //     ptr1[i] = malloc(128 - i);
    // }
    // for (int i = 0; i < 55; i += 2) {
    //     free(ptr1[i]);
    // }
    
    // for (int i = 0; i < 50; i++) {
    //     ptr2[i] = malloc(1024 - i * 3);
    // }
    // for (int i = 0; i < 25; i += 2) {
    //     free(ptr2[i]);
    // }

    for (int i = 0; i < 10; i++) {
        ptr3[i] = malloc(5000 - i * 499);
    }
    for (int i = 0; i < 5; i += 2) {
        free(ptr3[i]);
    }
    for (int i = 0; i < 10; i++) {
        ptr4[i] = malloc(5000 - i * 499);
    }
    for (int i = 0; i < 5; i += 2) {
        free(ptr4[i]);
    }

    show_alloc_mem();
    return 0;
}
