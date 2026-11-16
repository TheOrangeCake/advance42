/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   block.c                                            :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/15 22:33:59 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/16 22:54:03 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

t_block *find_block(t_block *head, void *ptr) {
    while (head) {
        if ((void *)(head + 1) == ptr) {
            return head;
        }
        head = head->next;
    }
    return NULL;
}

t_block *split_block(t_block *block, size_t size) {
	t_block	*new_block;

	new_block = (t_block*)((char *)block + sizeof(t_block) + size);
	new_block->length = block->length - sizeof(t_block) - size;
	new_block->free = FREE;
	new_block->prev = block;
	new_block->next = block->next;

	if (block->next) {
		block->next->prev = new_block;
	}
	block->length = sizeof(t_block) + size;
	block->free = FREE;
	block->next = new_block;

	return block;
}
