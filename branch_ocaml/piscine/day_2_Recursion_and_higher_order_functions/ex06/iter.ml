(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   iter.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:52:17 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:52:27 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let rec iter f x n =
  if n < 0
    then -1
  else if n = 0
    then x
  else
    iter f (f x) (n - 1)

let multi = fun x -> x * x
let multi2 = fun x -> x * 2

(* let () =
    assert (iter multi 2 4 = 65536);
    assert (iter multi2 2 4 = 32);
    assert (iter multi 2 (-10) = -1);
    assert (iter multi 2 0 = 2); *)
