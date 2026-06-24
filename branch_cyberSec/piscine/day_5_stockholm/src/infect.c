/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   infect.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/18 23:26:24 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/24 20:19:49 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "stockholm.h"

void spread(DIR *dir, char *base_path, bool s_flag, unsigned char *key);

int infect(char *key, bool s_flag) {
	char	infect_dir[PATH_MAX];
	char	*home;
	DIR		*dir;
	unsigned char	*encrypt_key;

	encrypt_key = load_key(key, s_flag);
	if (!encrypt_key)
		return -1;
	
	home = getenv("HOME");
	if (!home) {
		if (!s_flag)
			fprintf(stderr, "Error: HOME is not set\n");
		free(encrypt_key);
		return -1;
	}
	snprintf(infect_dir, sizeof(infect_dir), "%s/infection", home);

	dir = opendir(infect_dir);
	if (!dir) {
		if (!s_flag)
			fprintf(stderr, "Error: failed to open %s: %s\n", infect_dir, strerror(errno));
		free(encrypt_key);
		return -1;
	}

	spread(dir, infect_dir, s_flag, encrypt_key);
	closedir(dir);
	free(encrypt_key);
	return 0;
}

void spread(DIR *dir, char *base_path, bool s_flag, unsigned char *key) {
	struct dirent	*dp;
	char			path[PATH_MAX];
	DIR				*sub_dir;

	(void)key;
	while ((dp = readdir(dir)) != NULL) {
		if (strcmp(dp->d_name, ".") == 0 || strcmp(dp->d_name, "..") == 0)
			continue;
		snprintf(path, sizeof(path), "%s/%s", base_path, dp->d_name);
		if (dp->d_type == DT_DIR) {
			sub_dir = opendir(path);
			if (!sub_dir)
				continue;
			spread(sub_dir, path, s_flag, key);
			closedir(sub_dir);
		} else {
			// TODO: check extension, encrypt, rename
			if (!s_flag)
				printf("Processing: %s\n", path);
		}
	}
}
