/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   realloc.c                                          :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:20:05 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/20 22:43:57 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

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
