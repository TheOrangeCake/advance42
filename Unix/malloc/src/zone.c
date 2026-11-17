/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   zone.c                                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/15 22:34:03 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/17 23:29:15 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

t_zone *create_new_zone(size_t size) {
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
    block->length = size - sizeof(t_zone);
    block->free = FREE;
    block->prev = NULL;
    block->next = NULL;

    zone->length = size;
    zone->used = 0;
    zone->blocks = block;
    zone->next = NULL;
    return zone;
}

int add_zone(t_zone **head, size_t limit) {
    t_zone  *zone;
    size_t  size;

    size = MIN_ALLOC * (limit + sizeof(t_block)) + sizeof(t_zone);
    size = round_up(size, g_malloc.page_size);
    zone = create_new_zone(size);
    if (!zone) {
        return -1;
    }
    zone->next = *head;
    *head = zone;
    return 0;
}

void free_zone(t_zone **head, t_zone *zone) {
    t_zone  *prev;

	// If only 1 zone -> Keep
    if (*head == zone && !zone->next) {
        return;
    }

	// If multiple zone and first zone is empty
    if (*head == zone) {
        *head = zone->next;
    } else {
        prev = *head;
        while (prev->next != zone) {
            prev = prev->next;
        }
        prev->next = zone->next;
    }

    g_malloc.total_allocated -= zone->length;
    munmap((void *)zone, zone->length);
}
