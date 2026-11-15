/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   utils.c                                            :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 22:32:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/15 10:21:39 by hoannguy         ###   ########.fr       */
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

size_t round_up_page(size_t size, size_t p_size) {
	return ((size + p_size - 1) / p_size) * p_size;
}
