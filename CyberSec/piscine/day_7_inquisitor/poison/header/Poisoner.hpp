/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Poisoner.hpp                                       :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/07 23:15:39 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/10 13:18:03 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef POISONER_HPP
#define POISONER_HPP
#include "inquisitor.hpp"
#include "Victims.hpp"

class Poisoner {
	private:
		pcpp::PcapLiveDevice	*device;
		pcpp::MacAddress		poison_mac;
		pcpp::Packet			poisoned_server_packet;
		pcpp::Packet			poisoned_client_packet;
		pcpp::Packet			good_client_packet;
		pcpp::Packet			good_server_packet;
		std::atomic<bool>		stop;
		
		pcpp::Packet create_packet(
			pcpp::MacAddress m_src,
			pcpp::MacAddress m_dest,
			pcpp::IPv4Address i_src,
			pcpp::IPv4Address i_dest
		);

	public:
		Poisoner(Victims &victims, pcpp::PcapLiveDevice *device);
		~Poisoner();

		void setup();
		void poison();
		void stop_poison();
		void restore();
};

#endif