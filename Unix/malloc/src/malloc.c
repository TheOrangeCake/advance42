/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   malloc.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/10/27 20:19:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/08 22:02:42 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

// allocate size memory and return the pointer to
// the located memory. The memory is not initialized.
// If size == 0, still return a pointer that can be freed
// by free()
void *malloc(size_t size) {
    if (size == 0)
        return NULL;
    return NULL;
}

// int main(void) {
//     malloc(1);
//     return 0;
// }
