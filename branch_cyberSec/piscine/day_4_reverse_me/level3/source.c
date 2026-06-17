/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   source.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/17 23:15:43 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/17 23:42:05 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

int	___syscall_malloc(void)
{
	puts("Nope.");
	exit(1);
}

int	_____syscall_malloc(void)
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
		___syscall_malloc();
	if (input[1] != '2')
		___syscall_malloc();
	if (input[0] != '4')
		___syscall_malloc();
	fflush(stdin);
	memset(str, 0, 9);
	str[0] = '*';
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
	switch (strcmp(str, "********"))
	{
		case -2:
			___syscall_malloc();
			break;
		case -1:
			___syscall_malloc();
			break;
		case 0:
			_____syscall_malloc();
			break;
		case 1:
			___syscall_malloc();
			break;
		case 2:
			___syscall_malloc();
			break;
		case 3:
			___syscall_malloc();
			break;
		case 4:
			___syscall_malloc();
			break;
		case 5:
			___syscall_malloc();
			break;
		case 115:
			___syscall_malloc();
			break;
		default:
			___syscall_malloc();
			break;
	}
	return (0);
}

// main()
// printf("Please enter key: ")
// scanf() -> store in a buffer
// validation check for 42
// fflush
// memset
// set first char to *
// loop -> atoi() to transform chunk of 3 bytes to char, check with strlen()
// strcmp()
// switch case
// puts("Good job.\n") if success
// puts("Nope.\n") if fail -> exit()