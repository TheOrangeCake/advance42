/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Sniffer.hpp                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/08 23:18:59 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/09 10:51:28 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef SNIFFER_HPP
#define SNIFFER_HPP
#include "inquisitor.hpp"
#include "Victims.hpp"
#include "Poisoner.hpp"

class Sniffer {
	private:
		Victims					&victims;
		Poisoner				&poison;
		pcpp::PcapLiveDevice	*device;
		
		static void on_packet_arrives(pcpp::RawPacket *packet, pcpp::PcapLiveDevice* dev, void* cookie);
		void set_filters();

	public:
		Sniffer(Victims &victims, Poisoner &poison, pcpp::PcapLiveDevice *device);
		~Sniffer();
		
		void sniff();
		void end();
};

#endif