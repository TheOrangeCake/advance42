/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   desinfect.c                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/18 23:26:07 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/24 20:06:00 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "stockholm.h"

int desinfect(char *key, bool s_flag) {
	unsigned char	*encrypt_key;

	encrypt_key = load_key(key, s_flag);
	if (!encrypt_key)
		return -1;
	
	printf("DESINFECT ME");
	return 0;
}