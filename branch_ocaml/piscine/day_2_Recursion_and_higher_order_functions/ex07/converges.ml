(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   converges.ml                                       :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 13:17:31 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 14:09:50 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let rec converges f x n =
  if n < 0
    then false
  else
    let result = f x in
    if result = x
      then true
    else
      converges f result (n - 1)

let div = fun x -> x / 2

let double = fun x -> ( * ) 2 x

let concat = fun str -> str ^ "42"

(* let () =
      assert (converges double 2 5 = false);
      assert (converges div 2 3 = true);
      assert (converges div 2 2 = true);
      assert (converges div 8 2 = false);
      assert (converges div 2 (-2) = false);
      assert (converges concat "test" 2 = false) *)
