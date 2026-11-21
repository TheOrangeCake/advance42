/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   free.c                                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:20:11 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/21 17:24:39 by hoannguy         ###   ########.fr       */
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

void internal_free(void *ptr) {
    t_block *block;
    t_zone  *zone;

    if (!ptr) {
        return;
    }

    block = find_block(g_malloc.large, ptr);
    if (block) {
        free_large(block);
        return;
    }

    block = find_in_zone(g_malloc.tiny, ptr, &zone);
    if (block) {
        free_not_large(&g_malloc.tiny, zone, block);
        return;
    }

    block = find_in_zone(g_malloc.small, ptr, &zone);
    if (block) {
        free_not_large(&g_malloc.small, zone, block);
    }
}

void free(void *ptr) {
    pthread_mutex_lock(&g_mutex);
    internal_free(ptr);
    pthread_mutex_unlock(&g_mutex);
}
