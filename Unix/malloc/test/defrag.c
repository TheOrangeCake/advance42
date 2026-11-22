/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   defrag.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/22 12:00:00 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/22 09:11:16 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"
#include <string.h>
#include <stdio.h>
#include <stdarg.h>

#define N 7
#define UNIT 16
#define TAIL_MAX 8

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

static char block_letter(t_block *b, void **ptrs) {
    for (int i = 0; i < N; i++) {
        if (ptrs[i] == (void *)(b + 1)) {
            return 'A' + i;
        }
    }
    return '?';
}

static int fill(char *buf, int pos, char c, size_t count) {
    for (size_t i = 0; i < count; i++) {
        buf[pos++] = c;
    }
    return pos;
}

// One char = 16 bytes. H = block header, letter = used data, . = free data.
static void print_bar(t_zone *zone, void **ptrs) {
    char    buf[1024];
    int     pos;
    size_t  data;

    pos = 0;
    for (t_block *b = zone->blocks; b; b = b->next) {
        data = (b->length - sizeof(t_block)) / UNIT;
        buf[pos++] = '|';
        pos = fill(buf, pos, 'H', sizeof(t_block) / UNIT);
        if (b->free == FREE && !b->next && data > TAIL_MAX) {
            pos = fill(buf, pos, '.', TAIL_MAX);
            pos = fill(buf, pos, '>', 3);
        } else if (b->free == FREE) {
            pos = fill(buf, pos, '.', data);
        } else {
            pos = fill(buf, pos, block_letter(b, ptrs), data);
        }
    }
    buf[pos++] = '|';
    buf[pos++] = '\n';
    write(1, "  ", 2);
    write(1, buf, pos);
}

static void print_map(const char *msg, void **ptrs) {
    t_zone  *zone;
    int     i;

    title(msg);
    zone = g_malloc.tiny;
    if (!zone) {
        print("  TINY : not initialized\n");
        return;
    }
    print("  TINY zone %p (1 char = %d bytes, H = header, . = free)\n\n",
        zone, UNIT);
    print_bar(zone, ptrs);
    print("\n");
    i = 0;
    for (t_block *b = zone->blocks; b; b = b->next) {
        if (b->free == FREE) {
            print("  block %d  %p  FREE      %5zu bytes\n",
                i, b + 1, b->length - sizeof(t_block));
        } else {
            print("  block %d  %p  USED (%c)  %5zu bytes  (requested %zu)\n",
                i, b + 1, block_letter(b, ptrs),
                b->length - sizeof(t_block), b->requested);
        }
        i++;
    }
}

static void free_ptr(void **ptrs, int i) {
    print("\n  free %c", 'A' + i);
    free(ptrs[i]);
    ptrs[i] = NULL;
}

int main(void) {
    void    *ptrs[N] = {0};
    size_t  sizes[6] = {16, 32, 48, 64, 32, 16};

    for (int i = 0; i < 6; i++) {
        ptrs[i] = malloc(sizes[i]);
    }
    print_map("MALLOC 6 TINY (A-F)", ptrs);

    free_ptr(ptrs, 1);
    free_ptr(ptrs, 3);
    print_map("FREE B AND D -> 2 SEPARATE HOLES", ptrs);

    free_ptr(ptrs, 2);
    print_map("FREE C -> MERGES WITH B AND D INTO 1 HOLE", ptrs);

    free_ptr(ptrs, 5);
    print_map("FREE F -> MERGES WITH THE FREE TAIL", ptrs);

    ptrs[6] = malloc(96);
    print("\n  malloc 96 -> G");
    print_map("MALLOC G (96) -> REUSES THE HOLE, REST IS SPLIT OFF", ptrs);

    for (int i = 0; i < N; i++) {
        free(ptrs[i]);
        ptrs[i] = NULL;
    }
    print_map("FREE ALL -> 1 BIG FREE BLOCK", ptrs);
    return 0;
}
