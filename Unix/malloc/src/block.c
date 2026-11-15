/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   block.c                                            :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/15 22:33:59 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/15 23:16:07 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"

t_block *find_block(t_block *head, void *ptr) {
    while (head) {
        if ((void *)(head + 1) == ptr) {
            return head;
        }
        head = head->next;
    }
    return NULL;
}
