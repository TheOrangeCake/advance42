(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   repeat_x.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:52:33 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:52:34 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let repeat_x n =
  if n < 0
    then
      "Error"
    else
      let rec repeat x acc =
        if x <= 0
          then
            acc
        else
          repeat (x - 1) (acc ^ "x")
      in repeat n ""

(* let () =
  assert (repeat_x (-1) = "Error");
  assert (repeat_x 0 = "");
  assert (repeat_x 1 = "x");
  assert (repeat_x 2 = "xx");
  assert (repeat_x 5 = "xxxxx");
  assert (repeat_x 2 = "xxx"); *)
