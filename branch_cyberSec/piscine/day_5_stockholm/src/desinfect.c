/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   desinfect.c                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/18 23:26:07 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/28 17:05:02 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "stockholm.h"

void suppress(DIR *dir, char *base_path, bool s_flag, unsigned char *key);
bool decrypt_file(char *source_file, unsigned char *key);

int desinfect(char *key, bool s_flag) {
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

	suppress(dir, infect_dir, s_flag, encrypt_key);
	closedir(dir);
	free(encrypt_key);
	return 0;
}

void suppress(DIR *dir, char *base_path, bool s_flag, unsigned char *key) {
	struct dirent	*dp;
	char			path[PATH_MAX];
	DIR				*sub_dir;

	while ((dp = readdir(dir)) != NULL) {
		if (strcmp(dp->d_name, ".") == 0 || strcmp(dp->d_name, "..") == 0)
			continue;
		snprintf(path, sizeof(path), "%s/%s", base_path, dp->d_name);
		if (dp->d_type == DT_DIR) {
			sub_dir = opendir(path);
			if (!sub_dir)
				continue;
			suppress(sub_dir, path, s_flag, key);
			closedir(sub_dir);
		} else {
			if (!is_encrypted(dp->d_name)) {
				if (!s_flag)
					printf("[Skip\t\t] %s\n", path);
				continue;
			}
			if (!decrypt_file(path, key)) {
				if (!s_flag)
					printf("[Fail\t\t] %s\n", path);
				continue;
			}

			if (!s_flag)
				printf("[Decrypted\t] %s\n", path);
		}
	}
}

bool decrypt_file(char *source_file, unsigned char *key) {
	unsigned char	header[crypto_secretstream_xchacha20poly1305_HEADERBYTES];
	crypto_secretstream_xchacha20poly1305_state	state;
	FILE	*source;
	char	target_file[PATH_MAX];
	int		fd;
	FILE	*target;
	unsigned long long	out_len;
	size_t	rlen;
	int		eof;
	unsigned char	tag;
	unsigned char  buf_in[CHUNK_SIZE + crypto_secretstream_xchacha20poly1305_ABYTES];
	unsigned char  buf_out[CHUNK_SIZE];
	

	source = fopen(source_file, "rb");
	if (source == NULL)
		return false;

	strncpy(target_file, source_file, PATH_MAX - 1);
	target_file[PATH_MAX - 1] = '\0';
	*strrchr(target_file, '.') = '\0';
	fd = open(target_file, O_WRONLY | O_CREAT | O_TRUNC, 0600);
	if (fd < 0) {
		fclose(source);
		return false;
	}

	target = fdopen(fd, "wb");
	if (target == NULL) {
		fclose(source);
		close(fd);
		return false;
	}

	fread(header, 1, sizeof header, source);
	if (crypto_secretstream_xchacha20poly1305_init_pull(&state, header, key) != 0) {
		clean_up(target, source, target_file);
		return false;
	}

	do {
		rlen = fread(buf_in, 1, sizeof buf_in, source);
		eof = feof(source);
		if (!eof && ferror(source)) {
			clean_up(target, source, target_file);
			return false;
		}
		
		if (crypto_secretstream_xchacha20poly1305_pull(&state, buf_out, &out_len, &tag,
													buf_in, rlen, NULL, 0) != 0) {
			clean_up(target, source, target_file);
			return false;
		}
		if (tag == crypto_secretstream_xchacha20poly1305_TAG_FINAL) {
			if (!eof) {
				clean_up(target, source, target_file);
				return false;
			}
		} else {
			if (eof) {
				clean_up(target, source, target_file);
				return false;
			}
		}
		fwrite(buf_out, 1, (size_t) out_len, target);
	} while (!eof);
	
	fclose(target);
	fclose(source);
	remove(source_file);
	return true;
}
