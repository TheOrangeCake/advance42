(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_test_sign.ml                                    :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:53:59 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:54:01 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let ft_test_sign x = 
	if x >= 0
	then print_endline "positive"
	else print_endline "negative"

let () =
	ft_test_sign 42;
	ft_test_sign 0;
	ft_test_sign (-42);
	ft_test_sign max_int;
	ft_test_sign min_int
