/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   malloc.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:19:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/21 14:39:16 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

t_malloc    g_malloc;

static int init() {
    g_malloc.page_size = sysconf(_SC_PAGE_SIZE);
    if (g_malloc.page_size <= 0) {
        g_malloc.page_size = 0;
        errno = ENOMEM;
        return -1;
    }
    g_malloc.total_allocated = 0;

    g_malloc.tiny = NULL;
    g_malloc.small = NULL;
    g_malloc.large = NULL;

    return 0;
}

// Check if any block free and fit. If free block is bigger than
// size then split block to fit better.
// If no free space or no zone, then create new zone. 
static void *handle_not_large(size_t size, int type) {
    t_zone  **head;
    size_t  limit;

    if (type == TINY) {
        limit = L_TINY;
        head = &g_malloc.tiny;
    } else {
        limit = L_SMALL;
        head = &g_malloc.small;
    }

    size = round_up(size, ALIGNMENT);

    for (t_zone *zone = *head; zone; zone = zone->next) {
        for (t_block *b = zone->blocks; b; b = b->next) {
            if (b->free == FREE && b->length - sizeof(t_block) >= size) {
                if (b->length - sizeof(t_block) - size >= sizeof(t_block) + ALIGNMENT) {
                    b = split_block(b, size);
                }
                b->free = USED;
                zone->used += b->length;
                return b + 1;
            }
        }
    }
    
    // No fit or no zone -> create a new zone and retry
    if (add_zone(head, limit) < 0) {
        return NULL;
    }

    return handle_not_large(size, type);
}

// Allocate a block then insert it to the front of the linked list.
// Return the pointer to the free space.
static void *handle_large(size_t size) {
    t_block *block;
    
    size += sizeof(t_block);
    size = round_up(size, g_malloc.page_size);

    if (is_space_left(g_malloc.total_allocated, size) == false || size == 0) {
        errno = ENOMEM;
        return NULL;
    }

    block = mmap(NULL, size,
        PROT_READ | PROT_WRITE,
        MAP_PRIVATE | MAP_ANONYMOUS,
        -1, 0
    );
    if (block == MAP_FAILED) {
        errno = ENOMEM;
        return NULL;
    }

    g_malloc.total_allocated += size;

    block->length = size;
    block->free = USED;
    insert_block(block);

    return block + 1;
}

void *malloc(size_t size) {
    void    *ptr;

    if (g_malloc.page_size == 0) {
        if (init() < 0) {
            return NULL;
        }
    }
    if (size == 0) {
        return NULL;
    }
    if (size > SIZE_MAX - sizeof(t_block) - g_malloc.page_size) {
        errno = ENOMEM;
        return NULL;
    } else if (size <= L_TINY) {
        ptr = handle_not_large(size, TINY);
    } else if (size <= L_SMALL) {
        ptr = handle_not_large(size, SMALL);
    } else {
        ptr = handle_large(size);
    }
    if (ptr) {
        ((t_block *)ptr - 1)->requested = size;
    }
    return ptr;
}
