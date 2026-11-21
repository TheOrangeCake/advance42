/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   libft_malloc.h                                     :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 22:25:45 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/21 17:27:13 by hoannguy         ###   ########.fr       */
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
#include <errno.h>
#include <pthread.h>

#define FREE 1
#define USED 2

#define TINY 1
#define SMALL 2
#define LARGE 3

// L for Limit
#define MIN_ALLOC 100
#define L_TINY 128
#define L_SMALL 1024

#define ALIGNMENT 16

typedef struct s_block	t_block;
typedef struct s_zone	t_zone;

struct s_block {
	size_t	length;
	size_t	requested;
	int		free;
	t_block	*prev;
	t_block	*next;
	void	*padding;
};

struct s_zone {
	size_t	length;
	size_t	used;
	t_block	*blocks;
	t_zone	*next;
};

typedef struct s_malloc {
	long	page_size;
	size_t	total_allocated;
	t_zone	*tiny;
	t_zone	*small;
	t_block	*large;
} t_malloc;

extern t_malloc			g_malloc;
extern pthread_mutex_t	g_mutex;

void	*malloc(size_t size);
void	free(void *ptr);
void	*realloc(void *ptr, size_t size);
void	*calloc(size_t n, size_t size);

void	*internal_malloc(size_t size);
void	internal_free(void *ptr);

t_zone	*create_new_zone(size_t size);
int		add_zone(t_zone **head, size_t limit);
void	free_zone(t_zone **head, t_zone *zone);

t_block	*find_block(t_block *head, void *ptr);
t_block	*find_in_zone(t_zone *head, void *ptr, t_zone **zone_out);
t_block	*find_location(void *ptr);
t_block	*split_block(t_block *block, size_t size);
void	merge_blocks(t_block *block);
void	insert_block(t_block *block);

void	show_alloc_mem();

bool	is_space_left(size_t total_allocated, size_t len);
size_t	round_up(size_t size, size_t multiple);
size_t	min(size_t a, size_t b);
void	*ft_memcpy(void *dest, const void *src, size_t n);
void	print_ptr(void *ptr);
void	print_size(size_t n);

#endif
