/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   utils.c                                            :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 22:32:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/20 23:47:56 by hoannguy         ###   ########.fr       */
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
        return dest;
    }

    while (n > 0) {
        *ptrdest = *ptrsrc;
        ptrdest++;
        ptrsrc++;
        n--;
    }
    return dest;
}

size_t ft_strlen(const char *s) {
	size_t	i;

	i = 0;
	while (s[i]) {
		i++;
	}
	return i;
}

void print_ptr(void *ptr) {
	char		*hex;
	char		buf[16];
	uintptr_t	val;
	int			i;

	hex = "0123456789abcdef";
	val = (uintptr_t)ptr;
	i = 16;
	if (val == 0) {
		buf[--i] = '0';
	}
	while (val != 0) {
		buf[--i] = hex[val % 16];
		val /= 16;
	}
	write(1, "0x", 2);
	write(1, buf + i, 16 - i);
}

void print_size(size_t n) {
	char	buf[20];
	int		i;

	i = sizeof(buf);
	if (n == 0) {
		buf[--i] = '0';
	}
	while (n != 0) {
		buf[--i] = '0' + n % 10;
		n /= 10;
	}
	write(1, buf + i, sizeof(buf) - i);
}
