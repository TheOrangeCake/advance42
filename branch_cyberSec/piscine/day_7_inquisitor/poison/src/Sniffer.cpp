/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Sniffer.cpp                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/08 23:20:32 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/09 11:19:21 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "inquisitor.hpp"
#include "Sniffer.hpp"

Sniffer::Sniffer(Victims &victims, Poisoner &poison, pcpp::PcapLiveDevice *device)
	: victims(victims), poison(poison), device(device) {
}

Sniffer::~Sniffer() {
	
}

void Sniffer::on_packet_arrives(pcpp::RawPacket *packet, pcpp::PcapLiveDevice* dev, void* cookie) {
	(void) dev;
	(void) cookie;

	pcpp::Packet parsed_packet(packet);
	pcpp::FtpRequestLayer *ftp_req = parsed_packet.getLayerOfType<pcpp::FtpRequestLayer>();
	if (ftp_req == NULL)
		return;
	
	pcpp::FtpRequestLayer::FtpCommand cmd = ftp_req->getCommand();
	if (cmd == pcpp::FtpRequestLayer::FtpCommand::APPE ||
		cmd == pcpp::FtpRequestLayer::FtpCommand::RETR ||
		cmd == pcpp::FtpRequestLayer::FtpCommand::STOR) {
		std::string file_name = ftp_req->getCommandOption();
		std::cout << ftp_req->getCommandString() << " > " << file_name << std::endl;	
	}
}

void Sniffer::set_filters() {
	pcpp::IPFilter serverFilter(this->victims.get_Ip(SERVER), pcpp::SRC_OR_DST);
	pcpp::IPFilter clientFilter(this->victims.get_Ip(CLIENT), pcpp::SRC_OR_DST);
	pcpp::PortFilter portFilter(FTP_PORT, pcpp::SRC_OR_DST);
	pcpp::AndFilter andFilter;
	andFilter.addFilter(&serverFilter);
	andFilter.addFilter(&clientFilter);
	andFilter.addFilter(&portFilter);
	this->device->setFilter(andFilter);
}

void Sniffer::sniff() {
	this->set_filters();

	if (!this->device->startCapture(Sniffer::on_packet_arrives, NULL))
		throw std::runtime_error("Error: cannot start capture on " + device->getName());
	std::cout << std::endl << "Packet capturing started" << std::endl;
}

void Sniffer::end() {
	this->device->stopCapture();
}
