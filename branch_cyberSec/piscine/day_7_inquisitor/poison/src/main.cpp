/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   main.cpp                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/28 18:34:55 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/07 23:15:07 by hoannguy         ###   ########.fr       */
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

	// quick test argv validation ./inquisitor 51.154.48.225 00:aa:29:ff:1a:fc 51.154.48.225 00:aa:29:ff:1a:fc
	std::cout << "OK" << std::endl;

	// Pseudo
	// Get an instance of PcapLiveDeviceList
	// Get current PcapLiveDevice associated with the static Ip with getPcapLiveDeviceByIp
	// Get poison MAC address from PcapLiveDevice object
	// print current PcapLiveDevice stat (name, desc, mac, gateway, mtu and dns if exist) for extra info
	// Open the PcapLiveDevice
	// Create a callback to parse raw package and print out file name
	// Add filter to only capture from both victims IP
	// Call startCapture passing in the callback and void cookie because we dont need to store anything
	// 
	// Now once the capturing is up, time to send poisoned packages to victims
	// Since capturing is happening in another thread, on main thread we can loop
	// Each loop will sendPacket() and multiPlatformSleep() so we have a continous stream of poison to stop cache.
	// problem here is sendPacket() signature doesnt have target, so we will need to craft the package ourself
	// maybe we can craft the 2 packages only once before the loop since only the MAC matter and the content doesnt.
	// First poison to server: create L1 with src MAC (this poison) and dest MAC (server)
	// Create L2 with src IP (client IP) and dest IP (server IP)
	// Create new package with those 4 layers
	//  pcpp::Packet newPacket(100); newPacket.addLayer();
	// newPacket.computeCalculateFields();
	// now we just send this package over and over every few second


	// On ctrl C with pcpp::ApplicationEventHandler, stopCapture()
	// send good packages to victims
	// exit

	return 0;
}
