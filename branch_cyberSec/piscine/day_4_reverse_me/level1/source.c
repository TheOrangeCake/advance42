/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   source.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/17 22:46:31 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/17 22:54:18 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include <stdio.h>
#include <string.h>

int	main(void)
{
	char	input[100];
	char	str[] = "__stack_check";

	printf("Please enter key: ");
	scanf("%s", input);
	if (strcmp(input, str) == 0)
		printf("Good job.\n");
	else
		printf("Nope.\n");
	return (0);
}

// main()
// printf("Please enter key: ")
// scanf() -> store in a buffer
// strcmp(buffer, "__stack_check") -> compare buffer with the password
// printf("Good job.\n") if success
// printf("Nope.\n") if fail
