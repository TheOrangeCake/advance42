(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_is_palindrome.ml                                :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:56:40 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:56:40 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let ft_is_palindrome str =
  let len = String.length str in
  let rec palindrome ia ib =
    if ia >= ib
      then
        true
      else
        let ca = String.get str ia in
        let cb = String.get str ib in
        if ca = cb
          then
            palindrome (ia + 1) (ib - 1)
          else
            false
  in palindrome 0 (len - 1)

let () =
  if ft_is_palindrome "radar"
    then
      print_endline "Ok"
    else
      print_endline "Nope";
  if ft_is_palindrome "madam"
    then
      print_endline "Ok"
    else
      print_endline "Nope";
  if ft_is_palindrome "car"
    then
      print_endline "Ok"
    else
      print_endline "Nope";
  if ft_is_palindrome ""
    then
      print_endline "Ok"
    else
      print_endline "Nope"
