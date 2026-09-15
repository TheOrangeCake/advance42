/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Victims.hpp                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/06 22:16:13 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/08 16:13:52 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef VICTIMS_HPP
#define VICTIMS_HPP

#include "inquisitor.hpp"

class Victims {
	private:
		pcpp::IPv4Address	s_ip;
		pcpp::MacAddress	s_mac;
		pcpp::IPv4Address	c_ip;
		pcpp::MacAddress	c_mac;

		bool	s_ip_set = false;
		bool	s_mac_set = false;
		bool	c_ip_set = false;
		bool	c_mac_set = false;
		
	public:
		Victims();
		~Victims();

		pcpp::IPv4Address get_Ip(int victim);
		pcpp::MacAddress get_Mac(int victim);

		Victims& set_Ip(std::string ip, int victim);
		Victims& set_Mac(std::string mac, int victim);
};

#endif