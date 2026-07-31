(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_countdown.ml                                    :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:54:04 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:54:04 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let rec ft_countdown x =
	if x <= 0
	then (
		print_int 0;
		print_char '\n'
	) else (
		print_int x;
		print_char '\n';
		ft_countdown(x - 1)
	)

(* let () =
	ft_countdown 5;
	print_char '\n';
	ft_countdown 0;
	print_char '\n';
	ft_countdown (-10) *)
