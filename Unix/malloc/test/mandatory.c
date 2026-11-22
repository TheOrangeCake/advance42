/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   mandatory.c                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/22 08:01:23 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/22 09:12:36 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"
#include <string.h>
#include <stdio.h>
#include <stdarg.h>

#define N 5

typedef struct s_group {
    const char  *name;
    size_t      size[N];
    char        *ptr[N];
} t_group;

static void print(const char *fmt, ...) {
    char    buf[512];
    va_list args;
    int     len;

    va_start(args, fmt);
    len = vsnprintf(buf, sizeof(buf), fmt, args);
    va_end(args);
    if (len > (int)sizeof(buf) - 1) {
        len = sizeof(buf) - 1;
    }
    write(1, buf, len);
}

static void title(const char *msg) {
    print("\n==================== %s ====================\n", msg);
}

static void alloc_group(t_group *g) {
    for (int i = 0; i < N; i++) {
        g->ptr[i] = malloc(g->size[i]);
        if (g->ptr[i]) {
            // Write some stuffs in the allocated memory
            snprintf(g->ptr[i], g->size[i], "%s #%d", g->name, i);
        }
    }
}

static void print_group(t_group *g) {
    for (int i = 0; i < N; i++) {
        if (!g->ptr[i]) {
            print("  %s[%d] (freed)\n", g->name, i);
            continue;
        }
        print("  %s[%d] %p  %6zu bytes  \"%s\"\n",
            g->name, i, g->ptr[i], g->size[i], g->ptr[i]);
    }
}

static void free_one(t_group *g, int i) {
    print("  free %s[%d] (%zu bytes)\n", g->name, i, g->size[i]);
    free(g->ptr[i]);
    g->ptr[i] = NULL;
}

static void realloc_one(t_group *g, int i, size_t size, const char *more) {
    char    *new_ptr;

    new_ptr = realloc(g->ptr[i], size);
    if (!new_ptr) {
        print("  realloc %s[%d] FAILED\n", g->name, i);
        return;
    }
    print("  realloc %s[%d]: %zu -> %zu bytes | %p -> %p %s\n",
        g->name, i, g->size[i], size, g->ptr[i], new_ptr,
        g->ptr[i] == new_ptr ? "(in place)" : "(moved)");
    g->ptr[i] = new_ptr;
    g->size[i] = size;
    if (more) {
        strcat(g->ptr[i], more);
    } else {
        g->ptr[i][3] = '\0';
    }
}

static void free_group(t_group *g) {
    for (int i = 0; i < N; i++) {
        free(g->ptr[i]);
        g->ptr[i] = NULL;
    }
}

static void test_calloc(void) {
    int     *arr;

    arr = calloc(10, sizeof(int));
    if (!arr) {
        print("  calloc FAILED\n");
        return;
    }
    print("  calloc(10, sizeof(int)) %p:", arr);
    for (int i = 0; i < 10; i++) {
        print(" %d", arr[i]);
    }
    print("\n\n");
    show_alloc_mem();
    free(arr);
}

int main(void) {
    t_group tiny = {"tiny", {8, 13, 42, 100, 128}, {0}};
    t_group small = {"small", {129, 300, 512, 777, 1024}, {0}};
    t_group large = {"large", {1025, 4000, 5000, 12345, 100000}, {0}};

    title("MALLOC 5 TINY, 5 SMALL, 5 LARGE");
    alloc_group(&tiny);
    alloc_group(&small);
    alloc_group(&large);
    print_group(&tiny);
    print_group(&small);
    print_group(&large);
    print("\n");
    show_alloc_mem();

    title("FREE [1] AND [3] OF EACH");
    for (int i = 1; i < N; i += 2) {
        free_one(&tiny, i);
        free_one(&small, i);
        free_one(&large, i);
    }
    print("\n");
    show_alloc_mem();

    title("REALLOC");
    realloc_one(&tiny, 0, 64, " + more content");
    realloc_one(&tiny, 2, 2000, " -> now LARGE");
    realloc_one(&large, 4, 200000, " -> even LARGER");
    print_group(&tiny);
    print_group(&small);
    print_group(&large);
    print("\n");
    show_alloc_mem();

    title("CALLOC");
    test_calloc();

    title("FREE ALL");
    free_group(&tiny);
    free_group(&small);
    free_group(&large);
    print("\n");
    show_alloc_mem();
    return 0;
}
