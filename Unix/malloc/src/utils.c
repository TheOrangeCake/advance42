/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   utils.c                                            :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 22:32:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/20 22:44:19 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

bool is_space_left(size_t total_allocated, size_t len) {
	struct rlimit	r_limit;

	if (getrlimit(RLIMIT_AS, &r_limit) < 0) {
		return false;
	}
	if (r_limit.rlim_cur == RLIM_INFINITY) {
		return true;
	}

	if (total_allocated + len >= r_limit.rlim_cur) {
		return false;
	}
	return true;
}

size_t round_up(size_t size, size_t multiple) {
	return ((size + multiple - 1) / multiple) * multiple;
}

size_t min(size_t a, size_t b) {
    return a < b ? a : b;
}

void *ft_memcpy(void *dest, const void *src, size_t n) {
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
