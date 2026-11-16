/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   utils.c                                            :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 22:32:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/16 23:16:07 by hoannguy         ###   ########.fr       */
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
