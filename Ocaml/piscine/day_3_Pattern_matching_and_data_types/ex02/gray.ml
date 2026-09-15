(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   gray.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 11:04:38 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/02 17:35:51 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let gray n =
  if n < 0
    then (
      print_string ("Error");
      print_char '\n'
  ) else
    let rec build acc bits rev =
      match bits with
      | 0 -> acc
      | bits ->
          let (first, second) =
            if rev
              then ("1", "0")
            else ("0", "1")
          in build (acc ^ first) (bits - 1) false
             ^ " " ^ build (acc ^ second) (bits - 1) true

    in (
      print_string (build "" n false);
      print_char '\n'
    )

let () =
  gray 1;
  gray 2;
  gray 3;
  gray (-1);
  gray 0
