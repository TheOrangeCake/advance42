/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Victims.hpp                                        :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/06 22:16:13 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/06 23:24:00 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef VICTIMS_HPP
#define VICTIMS_HPP

#include "inquisitor.hpp"

class Victims {
	private:
		pcpp::IPv4Address	ip_src;
		pcpp::MacAddress	mac_src;
		pcpp::IPv4Address	ip_tar;
		pcpp::MacAddress	mac_tar;

		bool	ip_src_set = false;
		bool	mac_src_set = false;
		bool	ip_tar_set = false;
		bool	mac_tar_set = false;
		
	public:
		Victims();
		~Victims();

		pcpp::IPv4Address get_Ip(int victim);
		pcpp::MacAddress get_Mac(int victim);

		Victims& set_Ip(std::string ip, int victim);
		Victims& set_Mac(std::string mac, int victim);
};

#endif