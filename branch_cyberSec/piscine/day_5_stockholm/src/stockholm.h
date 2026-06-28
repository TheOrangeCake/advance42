/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   stockholm.h                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/18 21:59:31 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/28 16:38:15 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef STOCKHOLM_H
#define STOCKHOLM_H

#include <stdio.h>
#include <sodium.h>
#include <getopt.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/types.h>
#include <stdlib.h>
#include <limits.h>

#define KEY_LEN 32
#define CHUNK_SIZE 4096

int				desinfect(char *key, bool s_flag);
int				infect(char *key, bool s_flag);
int				generate(bool s_flag);
bool			is_affected_extension(char *ext);
bool			is_encrypted(char *file_name);
void			clean_up(FILE *target, FILE *source, char *target_file);
unsigned char	*load_key(char *arg, bool s_flag);

#endif