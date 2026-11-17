/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   free.c                                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:20:11 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/17 23:36:16 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

static void free_large(t_block *block) {
    if (block->prev) {
        block->prev->next = block->next;
    } else {
        g_malloc.large = block->next;
    }
    if (block->next) {
        block->next->prev = block->prev;
    }

    g_malloc.total_allocated -= block->length;
    
    munmap((void*)block, block->length);
}

static void free_not_large(t_zone **head, t_zone *zone, t_block *block) {
    if (block->free == FREE) {
        return;
    }

    block->free = FREE;
    zone->used -= block->length;
    merge_blocks(block);
    if (zone->used == 0) {
        free_zone(head, zone);
    }
}

static bool free_in_zones(t_zone **head, void *ptr) {
    t_block *block;

    for (t_zone *zone = *head; zone; zone = zone->next) {
        // Check if ptr is inside the zone, if not then continue
        if ((char *)ptr <= (char *)zone
            || (char *)ptr >= (char *)zone + zone->length) {
            continue;
        }
        block = find_block(zone->blocks, ptr);
        if (block) {
            free_not_large(head, zone, block);
        }
        return true;
    }
    return false;
}

void free(void *ptr) {
    t_block *block;
    
    if (!ptr) {
        return;
    }

    block = find_block(g_malloc.large, ptr);
    if (block) {
        free_large(block);
        return;
    }

    if (free_in_zones(&g_malloc.tiny, ptr)) {
        return;
    }

    free_in_zones(&g_malloc.small, ptr);
}
