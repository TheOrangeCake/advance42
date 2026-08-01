(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_power.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:54:08 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:54:09 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let ft_power x y =
	let rec power n =
		if n = 0
		then 1
		else x * power (n - 1)
	in power y

let () =
	print_endline(string_of_int(ft_power 2 4));
	print_endline(string_of_int(ft_power 3 0));
	print_endline(string_of_int(ft_power 0 5));


(* exception Invalid_input
let ft_power x y =
	if (x = 0 && y = 0) || x < 0 || y < 0
	then raise Invalid_input
	else (
		let rec power n =
			if n = 0
			then 1
			else x * power (n - 1)
		in power y
	)


let () =
	print_endline(string_of_int(ft_power 2 4));
	print_endline(string_of_int(ft_power 3 0));
	print_endline(string_of_int(ft_power 0 5));
	(try print_endline (string_of_int (ft_power 0 0))
		with Invalid_input -> print_endline "Invalid_input") *)
