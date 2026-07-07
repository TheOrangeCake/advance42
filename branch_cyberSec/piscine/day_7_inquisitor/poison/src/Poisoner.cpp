/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Poisoner.cpp                                       :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/07/07 23:23:41 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/07 23:34:29 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "inquisitor.hpp"
#include "Poisoner.hpp"

Poisoner::Poisoner(Victims &victims, pcpp::MacAddress poison_mac) {
	this->poison_mac = poison_mac;
	(void)victims;
}

Poisoner::~Poisoner() {
	
}
