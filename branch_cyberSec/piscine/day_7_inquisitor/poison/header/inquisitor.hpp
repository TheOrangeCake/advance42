/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   inquisitor.hpp                                     :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/28 18:29:33 by hoannguy          #+#    #+#             */
/*   Updated: 2026/07/10 15:37:58 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#ifndef INQUISITOR_HPP
#define INQUISITOR_HPP
#include <iostream>
#include <string>
#include <stdexcept>
#include <MacAddress.h>
#include <IpAddress.h>
#include <PcapLiveDeviceList.h>
#include <SystemUtils.h>
#include <Packet.h>
#include <EthLayer.h>
#include <ArpLayer.h>
#include <FtpLayer.h>
#include <TcpLayer.h>
#include <atomic>

#define SERVER 1
#define CLIENT 2
#define FTP_PORT 21

#endif