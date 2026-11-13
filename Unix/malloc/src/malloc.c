/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   malloc.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:19:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/14 00:16:01 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

t_malloc    g_malloc;

static t_zone *new_zone(size_t size) {
    t_zone *zone = mmap(
        NULL,
        size,
        PROT_READ | PROT_WRITE,
        MAP_PRIVATE | MAP_ANONYMOUS,
        -1,
        0
    );
    if (zone == NULL) {
        return NULL;
    }
    zone->length = size;
    zone->used = 0;
    zone->blocks = NULL;
    zone->next = NULL;
    return zone;
}

static int init() {
    size_t  tiny_size;
    size_t  small_size;

    g_malloc.page_size = sysconf(_SC_PAGE_SIZE);

    tiny_size = MIN_ALLOC * (TINY_MAX + sizeof(t_block)) + sizeof(t_zone);
    tiny_size = (tiny_size / g_malloc.page_size + 1) * g_malloc.page_size;
    g_malloc.tiny = new_zone(tiny_size);
    if (g_malloc.tiny == NULL) {
        return -1;
    }

    small_size = MIN_ALLOC * (SMALL_MAX + sizeof(t_block)) + sizeof(t_zone);
    small_size = (small_size / g_malloc.page_size + 1) * g_malloc.page_size;
    g_malloc.small = new_zone(small_size);
    if (g_malloc.small == NULL) {
        return -1;
    }
    return 0;
}

void *handle_not_large(size_t size, int type) {
    type += 0;
    size += 0;
    return NULL;
}

void *handle_large(size_t size) {
    size += 0;
    return NULL;
}

// allocate size memory and return the pointer to
// the located memory. The memory is not initialized.
// If size == 0, still return a pointer that can be freed by free()
void *malloc(size_t size) {
    if (g_malloc.page_size == 0) {
        if (init() < 0) {
            return NULL;
        }
    }
    if (size <= 0) {
        return NULL;
    } else if (size <= TINY_MAX) {
        return handle_not_large(size, TINY);
    } else if (size <= SMALL_MAX) {
        return handle_not_large(size, SMALL); 
    }
    return handle_large(size);
}

// int main(void) {
//     malloc(1);
//     return 0;
// }
