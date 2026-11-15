/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   libft_malloc.h                                     :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 22:25:45 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/15 19:32:53 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef LIBFT_MALLOC_H
#define LIBFT_MALLOC_H
#include <stddef.h>
#include <sys/mman.h>
#include <sys/resource.h>
#include <unistd.h>
#include <stdbool.h>
#include <stdint.h>

#define FREE 1
#define USED 2

#define TINY 1
#define SMALL 2
#define LARGE 3

// L for Limit
#define MIN_ALLOC 100
#define L_TINY 128
#define L_SMALL 1024

typedef struct s_block {
	size_t			length;
	int				free;
	int				zone;
	struct s_block	*prev;
	struct s_block	*next;
} t_block;

typedef struct s_zone {
	size_t			length;
	size_t			used;
	t_block			*blocks;
	struct s_zone	*next;
} t_zone;

typedef struct s_malloc {
	long			page_size;
	size_t			total_allocated;
	struct s_zone	*tiny;
	struct s_zone	*small;
	struct s_block	*large;
} t_malloc;

void	*malloc(size_t size);

bool	is_space_left(size_t total_allocated, size_t len);
size_t	round_up_page(size_t size, size_t p_size);

#endif
