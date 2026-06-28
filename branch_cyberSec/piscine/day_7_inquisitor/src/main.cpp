/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   main.cpp                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/28 18:34:55 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/29 00:18:01 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "inquisitor.hpp"

int main(int argc, char *argv[]) {
	if (argc < 5) {
		std::cerr << "Usage: " << argv[0] << " <IP-src> <MAC-src> <IP-target> <MAC-target>" << std::endl;
		return 1;
	}

	if (!pcpp::IPv4Address::isValidIPv4Address(argv[1]) || !pcpp::IPv4Address::isValidIPv4Address(argv[3])) {
		std::cerr << "Error: invalid IPv4 address." << std::endl;
		return 1;
	}
	pcpp::IPv4Address ip_src(argv[1]);
	pcpp::IPv4Address ip_tar(argv[3]);


	pcpp::MacAddress mac_src;
	pcpp::MacAddress mac_tar;
	try {
		mac_src = pcpp::MacAddress(argv[2]);
		mac_tar = pcpp::MacAddress(argv[4]);
	} catch (std::invalid_argument &e) {
		std::cerr << "Error: invalid MAC address." << std::endl;
		return 1;
	}

	// quick test ./inquisitor 51.154.48.225 00:aa:29:ff:1a:fc 51.154.48.225 00:aa:29:ff:1a:fc
	std::cout << "OK" << std::endl;

	return 0;
}
