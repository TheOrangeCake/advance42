/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   main.cpp                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/28 18:34:55 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/06 23:28:38 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "inquisitor.hpp"
#include "Victims.hpp"

int main(int argc, char *argv[]) {
	if (argc < 5) {
		std::cerr << "Usage: " << argv[0] << " <IP-src> <MAC-src> <IP-target> <MAC-target>" << std::endl;
		return 1;
	}

	Victims victims;
	try {
		victims.set_Ip(argv[1], SOURCE);
		victims.set_Mac(argv[2], SOURCE);
		victims.set_Ip(argv[3], TARGET);
		victims.set_Mac(argv[4], TARGET);
	} catch (std::invalid_argument &e) {
		std::cerr << e.what() << std::endl;
		return 1;
	}

	// quick test ./inquisitor 51.154.48.225 00:aa:29:ff:1a:fc 51.154.48.225 00:aa:29:ff:1a:fc
	std::cout << "OK" << std::endl;

	return 0;
}
