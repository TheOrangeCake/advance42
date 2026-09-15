(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_string_all.ml                                   :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:56:35 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:56:36 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let ft_string_all f str =
  let len = String.length str in
  let rec apply i =
    if i >= 0
      then
        let c = String.get str i in
        if (f c) = true
          then
            apply (i - 1)
          else
            false
      else
        true
  in apply (len - 1)

let is_digit c =
  c >= '0' && c <= '9'

let () =
  if (ft_string_all is_digit "123s45") = true
    then
      print_endline "Ok"
    else
      print_endline "Nope";
  if (ft_string_all is_digit "12345") = true
    then
      print_endline "Ok"
    else
      print_endline "Nope"
