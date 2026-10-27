/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   free.c                                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:20:11 by hoannguy          #+#    #+#             */
/*   Updated: 2026/10/27 22:27:48 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

void free(void *ptr) {
    (void*) (ptr = NULL);
    return;
}

// int main(void) {
//     free(NULL);
//     return 0;
// }
