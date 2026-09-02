(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   army.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/01 23:11:29 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/02 15:13:41 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class ['a] army =
  object
    val mutable members = ([]: 'a list)

    method add x = members <- x :: members

    method delete =
      match members with
      | [] -> print_endline "Army has no member"
      | hd :: tl ->
        (* print_endline ("Discharged: " ^ hd#to_string); *)
        members <- tl
end
