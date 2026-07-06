/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Victims.cpp                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/06 22:25:45 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/06 23:28:42 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "inquisitor.hpp"
#include "Victims.hpp"

Victims::Victims() {

}

Victims::~Victims() {
	
}

pcpp::IPv4Address Victims::get_Ip(int victim) {
	if (victim == SOURCE && this->ip_src_set)
		return this->ip_src;
	else if (victim == TARGET && this->ip_tar_set)
		return this->ip_tar;
	else
		throw std::invalid_argument("Error: Invalid victim or it's IP not set");
}

pcpp::MacAddress Victims::get_Mac(int victim) {
	if (victim == SOURCE && this->mac_src_set)
		return this->mac_src;
	else if (victim == TARGET && this->mac_tar_set)
		return this->mac_tar;
	else
		throw std::invalid_argument("Error: Invalid victim or it's MAC not set");
}


Victims& Victims::set_Ip(std::string ip, int victim) {
	if (ip.empty())
		throw std::invalid_argument("Error: Empty Ip");
	if (!pcpp::IPv4Address::isValidIPv4Address(ip))
		throw std::invalid_argument("Error: invalid IPv4 address: " + ip);
	if (victim == SOURCE) {
		this->ip_src = pcpp::IPv4Address(ip);
		this->ip_src_set = true;
	} else if (victim == TARGET) {
		this->ip_tar = pcpp::IPv4Address(ip);
		this->ip_tar_set = true;
	} else
		throw std::invalid_argument("Error: invalid victims");
	return *this;
}

Victims& Victims::set_Mac(std::string mac, int victim) {
	if (mac.empty())
		throw std::invalid_argument("Error: Empty Mac");
	if (victim == SOURCE) {
		this->mac_src = pcpp::MacAddress(mac);
		this->mac_src_set = true;
	} else if (victim == TARGET) {
		this->mac_tar = pcpp::MacAddress(mac);
		this->mac_tar_set = true;
	} else
		throw std::invalid_argument("Error: invalid victims");
	return *this;
}
