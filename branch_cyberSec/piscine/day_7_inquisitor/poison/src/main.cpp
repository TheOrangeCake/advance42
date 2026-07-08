/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   main.cpp                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/28 18:34:55 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/08 17:17:19 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "inquisitor.hpp"
#include "Victims.hpp"
#include "Poisoner.hpp"

// quick test argv validation ./inquisitor 51.154.48.225 00:aa:29:ff:1a:fc 51.154.48.225 00:aa:29:ff:1a:fc
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
		std::cerr << e.what() << std::endl;
		return 1;
	}

	// Note: eth0 only work in Docker
	pcpp::PcapLiveDevice *device = pcpp::PcapLiveDeviceList::getInstance().getDeviceByName("eth0");
	if (device == NULL) {
		std::cerr << "Cannot find interface for eth0" << std::endl;
		return 1;
	}
	std::cout
		<< "Interface info:" << std::endl
		<< "   Interface name:        " << device->getName() << std::endl // get interface name
		<< "   Interface description: " << device->getDesc() << std::endl // get interface description
		<< "   MAC address:           " << device->getMacAddress() << std::endl // get interface MAC address
		<< "   Default gateway:       " << device->getDefaultGateway() << std::endl; // get default gateway


	Poisoner poison(victims, device->getMacAddress());
	
	// Open the PcapLiveDevice
	// Create a callback to parse raw package and print out file name
	// Add filter to only capture from both victims IP
	// Call startCapture passing in the callback and void cookie because we dont need to store anything
	
	
	// Now once the capturing is up, time to send poisoned packages to victims
	// Since capturing is happening in another thread, on main thread we can loop
	// Each loop will sendPacket() and multiPlatformSleep() so we have a continous stream of poison to stop cache.
	// now we just send the poisoned package over and over every few second


	// On ctrl C with pcpp::ApplicationEventHandler, stopCapture()
	// send good packages to victims
	// exit

	return 0;
}
