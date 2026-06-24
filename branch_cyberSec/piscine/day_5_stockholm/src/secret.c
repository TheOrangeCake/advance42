/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   secret.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/19 16:58:25 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/24 20:16:51 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "stockholm.h"

int generate(bool s_flag) {
	unsigned char	key[KEY_LEN];
	char			hex[KEY_LEN * 2 + 1];
	const char*		save_file = "secret.hex";
	int				fd;
	
	if (!s_flag)
		printf("Generating secret...\n");
	randombytes_buf(key, sizeof key);
	sodium_bin2hex(hex, sizeof hex, key, sizeof key);

	fd = open(save_file, O_WRONLY | O_CREAT | O_TRUNC, 0600);
	if (fd < 0) {
		if (!s_flag)
			fprintf(stderr, "Error: fail to open %s\n", save_file);
		return -1;
	}

	FILE *f = fdopen(fd, "w");
	if (f == NULL) {
		if (!s_flag)
			fprintf(stderr, "Error: fdopen failed: %s\n", strerror(errno));
		close(fd);
		return -1;
	}

	if (fprintf(f, "%s\n", hex) < 0) {
		if (!s_flag)
			fprintf(stderr, "Error: failed writing to %s\n", save_file);
		fclose(f);
		return -1;
	}

	if (fclose(f) != 0) {
		if (!s_flag)
			fprintf(stderr, "Error: failed closing %s\n", save_file);
		return -1;
	}

	if (!s_flag)
		printf("New secret: %s\n", hex);
	
	return 0;
}
