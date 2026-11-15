/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   malloc.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:19:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/15 19:35:18 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

t_malloc    g_malloc;

static t_zone *new_zone(size_t size, int type) {
    t_zone  *zone;
    t_block *block;

    if (is_space_left(g_malloc.total_allocated, size) == false || size == 0) {
        return NULL;
    }

    zone = mmap(
        NULL,
        size,
        PROT_READ | PROT_WRITE,
        MAP_PRIVATE | MAP_ANONYMOUS,
        -1,
        0
    );
    if (zone == MAP_FAILED) {
        return NULL;
    }

    g_malloc.total_allocated += size;

    block = (t_block *)(zone + 1);
    block->length = size - sizeof(t_zone) - sizeof(t_block);
    block->free = FREE;
    block->zone = type;
    block->prev = NULL;
    block->next = NULL;

    zone->length = size;
    zone->used = 0;
    zone->blocks = block;
    zone->next = NULL;
    return zone;
}

static int init() {
    g_malloc.page_size = sysconf(_SC_PAGE_SIZE);
    if (g_malloc.page_size <= 0) {
        g_malloc.page_size = 0;
        return -1;
    }
    g_malloc.total_allocated = 0;

    g_malloc.tiny = NULL;
    g_malloc.small = NULL;
    g_malloc.large = NULL;

    return 0;
}

static int init_not_large(int type) {
    size_t  limit;
    size_t  size;
    t_zone  **head;

    if (type == TINY) {
        limit = L_TINY;
        head = &g_malloc.tiny;
    } else {
        limit = L_SMALL;
        head = &g_malloc.small;
    }

    size = MIN_ALLOC * (limit + sizeof(t_block)) + sizeof(t_zone);
    size = round_up_page(size, g_malloc.page_size);
    *head = new_zone(size, type);
    if (*head == NULL) {
        return -1;
    }
    return 0;
}

static void *handle_not_large(size_t size, int type) {
    size += 0;
    type += 0;

    if (type == TINY && !g_malloc.tiny) {
        if (init_not_large(TINY) < 0) {
            return NULL;
        }
    }
    if (type == SMALL && !g_malloc.small) {
        if (init_not_large(SMALL) < 0) {
            return NULL;
        }
    }

    // TODO

    return NULL;
}

// Allocate a block then insert it to the front of the linked list.
// Return the pointer to the free space.
static void *handle_large(size_t size) {
    t_block     *block;
    
    size += sizeof(t_block);
    size = round_up_page(size, g_malloc.page_size);
    
    if (is_space_left(g_malloc.total_allocated, size) == false || size == 0) {
        return NULL;
    }

    block = mmap(
        NULL,
        size,
        PROT_READ | PROT_WRITE,
        MAP_PRIVATE | MAP_ANONYMOUS,
        -1,
        0
    );
    if (block == MAP_FAILED) {
        return NULL;
    }

    g_malloc.total_allocated += size;

    block->length = size - sizeof(t_block);
    block->free = USED;
    block->zone = LARGE;
    block->prev = NULL;
    block->next = g_malloc.large;

    if (g_malloc.large) {
        g_malloc.large->prev = block;        
    }
    g_malloc.large = block;

    return block + 1;
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
    if (size == 0 || size > SIZE_MAX - sizeof(t_block) - g_malloc.page_size) {
        return NULL;
    } else if (size <= L_TINY) {
        return handle_not_large(size, TINY);
    } else if (size <= L_SMALL) {
        return handle_not_large(size, SMALL); 
    }
    return handle_large(size);
}

// int main(void) {
//     malloc(1);
//     return 0;
// }
