/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   stockholm.h                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/18 21:59:31 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/18 23:53:23 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef STOCKHOLM_H
#define STOCKHOLM_H

#include <stdio.h>
#include <sodium.h>
#include <getopt.h>
#include <stdbool.h>

int desinfect(char* key, bool s_flag);
int infect(bool s_flag);

#endif