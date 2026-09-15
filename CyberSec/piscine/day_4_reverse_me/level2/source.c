/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   source.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/17 22:55:22 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/17 23:23:49 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void	no(void)
{
	puts("Nope.");
	exit(1);
}

int	ok(void)
{
	return (puts("Good job."));
}

int	main(void)
{
	char	input[100];
	char	str[9];
	int		index1;
	int		index2;
	char	num[4];

	printf("Please enter key: ");
	if (scanf("%s", input) != 1)
		no();
	if (input[1] != '0')
		no();
	if (input[0] != '0')
		no();
	fflush(stdin);
	memset(str, 0, 9);
	str[0] = 'd';
	num[3] = '\0';
	index1 = 2;
	index2 = 1;
	while (strlen(str) < 8 && index1 < strlen(input))
	{
		num[0] = input[index1];
		num[1] = input[index1 + 1];
		num[2] = input[index1 + 2];
		str[index2] = (char)atoi(num);
		index1 += 3;
		index2 += 1;
	}
	str[index2] = '\0';
	if (strcmp(str, "delabere") == 0)
	{
		ok();
	}
	else
	{
		no();
	}
	return (0);
}

// main()
// printf("Please enter key: ")
// scanf() -> store in a buffer
// validation check for 00
// fflush
// memset
// set first char to d
// loop -> atoi() to transform chunk of 3 bytes to char, check with strlen()
// strcmp()
// puts("Good job.\n") if success
// puts("Nope.\n") if fail -> exit()
