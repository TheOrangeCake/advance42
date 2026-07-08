/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Victims.cpp                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/06 22:25:45 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/08 16:15:37 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "inquisitor.hpp"
#include "Victims.hpp"

Victims::Victims() {

}

Victims::~Victims() {
	
}

pcpp::IPv4Address Victims::get_Ip(int victim) {
	if (victim == SERVER && this->s_ip_set)
		return this->s_ip;
	else if (victim == CLIENT && this->c_ip_set)
		return this->c_ip;
	else
		throw std::invalid_argument("Error: Invalid victim or it's IP not set");
}

pcpp::MacAddress Victims::get_Mac(int victim) {
	if (victim == SERVER && this->s_mac_set)
		return this->s_mac;
	else if (victim == CLIENT && this->c_mac_set)
		return this->c_mac;
	else
		throw std::invalid_argument("Error: Invalid victim or it's MAC not set");
}


Victims& Victims::set_Ip(std::string ip, int victim) {
	if (ip.empty())
		throw std::invalid_argument("Error: Empty Ip");
	if (!pcpp::IPv4Address::isValidIPv4Address(ip))
		throw std::invalid_argument("Error: invalid IPv4 address: " + ip);
	if (victim == SERVER) {
		this->s_ip = pcpp::IPv4Address(ip);
		this->s_ip_set = true;
	} else if (victim == CLIENT) {
		this->c_ip = pcpp::IPv4Address(ip);
		this->c_ip_set = true;
	} else
		throw std::invalid_argument("Error: invalid victims");
	return *this;
}

Victims& Victims::set_Mac(std::string mac, int victim) {
	if (mac.empty())
		throw std::invalid_argument("Error: Empty Mac");
	if (victim == SERVER) {
		this->s_mac = pcpp::MacAddress(mac);
		this->s_mac_set = true;
	} else if (victim == CLIENT) {
		this->c_mac = pcpp::MacAddress(mac);
		this->c_mac_set = true;
	} else
		throw std::invalid_argument("Error: invalid victims");
	return *this;
}
