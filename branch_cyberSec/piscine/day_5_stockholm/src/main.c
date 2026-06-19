/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   main.c                                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/18 21:56:56 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/19 17:12:19 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "stockholm.h"

void print_help(bool s_flag);
void print_version(bool s_flag);

int main(int ac, char** av) {
	bool	h_flag = false;
	bool	v_flag = false;
	bool	g_flag = false;
	bool	r_flag = false;
	char*	r_key = NULL;
	bool	s_flag = false;
	int		opt;

	struct option long_options[] = {
		{"help",		no_argument,		0,	'h'},
		{"version",		no_argument,		0,	'v'},
		{"reverse",		required_argument,	0,	'r'},
		{"silent",		no_argument,		0,	's'},
		{"generate",	no_argument,		0,	'g'},
		{0,				0,					0,	0}
	};

	if (sodium_init() < 0) {
		printf("Error: Sodium library couldn't be initialized\n");
		return 1;
	}

	while ((opt = getopt_long(ac, av, ":hvgr:s", long_options, NULL)) != -1) {
		switch (opt) {
			case 'h':
				h_flag = true;
				break;
			case 'v':
				v_flag = true;
				break;
			case 'r':
				r_flag = true;
				r_key = optarg;
				break;
			case 's':
				s_flag = true;
				break;
			case 'g':
				g_flag = true;
				
				break;
			case ':':
				fprintf(stderr, "Option -%c requires a key\n", optopt);
				return 1;
			case '?':
				fprintf(stderr, "Unknown option: -%c\n", optopt);
				return 1;
		}
	}
	if (v_flag)
		print_version(s_flag);
	if (h_flag)
		print_help(s_flag);
	if (g_flag)
		return generate(s_flag);
	if (v_flag || h_flag || g_flag)
		return 0;
	if (r_flag) {
		return desinfect(r_key, s_flag);
	}
	return infect(s_flag);
}


void print_help(bool s_flag) {
	if (s_flag)
		return;
	printf("Usage: ./stockholm [OPTIONS]\n");
	printf("Options:\n");
	printf("	-h,	--help			Show this help message\n");
	printf("	-v,	--version		Show version\n");
	printf("	-g,	--generate		Generate new key\n");
	printf("	-r,	--reverse		Reverse the infection (requires key)\n");
	printf("	-s,	--silent		Suppress output\n");
}

void print_version(bool s_flag) {
	if (s_flag)
		return;
	printf("stockholm v1.0\n");
}
