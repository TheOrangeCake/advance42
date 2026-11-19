/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   realloc.c                                          :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:20:05 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/19 23:03:23 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

static void *ft_memcpy(void *dest, const void *src, size_t n) {
    unsigned char	*ptrdest;
    unsigned char	*ptrsrc;

    ptrdest = (unsigned char *)dest;
    ptrsrc = (unsigned char *)src;

    if (n == 0 || src == dest) {
        return (dest);
    }

    while (n > 0) {
        *ptrdest = *ptrsrc;
        ptrdest++;
        ptrsrc++;
        n--;
    }
    return (dest);
}

static size_t min(size_t a, size_t b) {
    return a < b ? a : b;
}

void *realloc(void *ptr, size_t size) {
    void    *new_ptr;
    t_block *block;
    size_t  old_len;

    if (!ptr) {
        return malloc(size);
    }
    if (size == 0) {
        errno = EINVAL;
        return NULL;
    }

    block = find_location(ptr);
    if (!block || block->free == FREE) {
        return NULL;
    }
    old_len = block->length - sizeof(t_block);

    new_ptr = malloc(size);
    if (!new_ptr) {
        return NULL;
    }

    new_ptr = ft_memcpy(new_ptr, ptr, min(old_len, size));
    free(ptr);
    
    return new_ptr;
}
