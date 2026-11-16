/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   free.c                                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:20:11 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/16 23:16:11 by hoannguy         ###   ########.fr       */
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

    return;
}
