/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   print.c                                            :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/19 22:06:35 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/21 15:40:25 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

static size_t print_block(t_block *block) {
	print_ptr(block + 1);
	write(1, " - ", 3);
	print_ptr((char *)(block + 1) + block->requested);
	write(1, " | Requested: ", 14);
	print_size(block->requested);
	write(1, " bytes | Usable: ", 17);
	print_size(block->length - sizeof(t_block));
	write(1, " bytes\n", 7);
	return block->requested;
}

static size_t print_blocks(t_block *block) {
	size_t	total;

	total = 0;
	while (block) {
		if (block->free == USED) {
			total += print_block(block);
		}
		block = block->next;
	}
	return total;
}

static void print_header(int type, void *addr) {
	if (type == TINY) {
		write(1, "TINY : ", 7);
	} else if (type == SMALL) {
		write(1, "SMALL : ", 8);
	} else {
		write(1, "LARGE : ", 8);
	}
	print_ptr(addr);
	write(1, "\n", 1);
}

static size_t print_zones(t_zone *zone, int type) {
	size_t	total;

	if (!zone) {
		if (type == TINY) {
			write(1, "TINY : not initialized\n", 23);
		} else {
			write(1, "SMALL : not initialized\n", 24);
		}
		return 0;
	}
	total = 0;
	while (zone) {
		print_header(type, zone);
		total += print_blocks(zone->blocks);
		zone = zone->next;
	}
	return total;
}

static size_t print_large(t_block *block) {
	size_t	total;

	if (!block) {
		write(1, "LARGE : empty\n", 14);
		return 0;
	}
	total = 0;
	while (block) {
		print_header(LARGE, block);
		total += print_block(block);
		block = block->next;
	}
	return total;
}

void show_alloc_mem() {
	size_t	total;

	total = 0;
	total += print_zones(g_malloc.tiny, TINY);
	total += print_zones(g_malloc.small, SMALL);
	total += print_large(g_malloc.large);

	write(1, "Total : ", 8);
	print_size(total);
	write(1, " bytes\n", 7);
}
