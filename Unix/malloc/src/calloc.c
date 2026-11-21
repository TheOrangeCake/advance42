/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   calloc.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/21 16:10:44 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/21 17:30:03 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

static void ft_bzero(void *s, size_t n) {
	unsigned char	*ptr;

	ptr = (unsigned char *)s;
	while (n > 0) {
		*ptr = '\0';
		n--;
		ptr++;
	}
}

static void *internal_calloc(size_t n, size_t size) {
    void    *ptr;
    size_t  len;

    if (n != 0 && SIZE_MAX / n < size) {
        errno = ENOMEM;
        return NULL;
    }

    len = n * size;

    ptr = internal_malloc(len);
    if (!ptr) {
        return NULL;
    }

    ft_bzero(ptr, len);

    return ptr;
}

void *calloc(size_t n, size_t size) {
    void    *pointer;

    pthread_mutex_lock(&g_mutex);
    pointer = internal_calloc(n, size);
    pthread_mutex_unlock(&g_mutex);
    return pointer;
}
