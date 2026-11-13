/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   libft_malloc.h                                     :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 22:25:45 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/13 22:28:45 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef LIBFT_MALLOC_H
#define LIBFT_MALLOC_H
#include <stddef.h>
#include <sys/mman.h>
#include <unistd.h>
#include <stdio.h>

#define FREE 1
#define USED 2

#define MIN_ALLOC 100
#define TINY_MAX 128
#define SMALL_MAX 1024

typedef struct s_block {
	size_t			length;
	int				free;
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
	struct s_zone	*tiny;
	struct s_zone	*small;
	struct s_zone	*big;
} t_malloc;

void	*malloc(size_t size);

#endif
