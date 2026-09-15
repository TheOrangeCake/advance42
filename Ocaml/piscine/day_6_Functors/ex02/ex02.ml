(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ex02.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/30 13:31:33 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/30 14:00:42 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

module type PAIR = sig val pair : (int * int) end
module type VAL = sig val x : int end

module type MAKEPROJECTION = functor (F: PAIR) -> VAL

module MakeFst : MAKEPROJECTION = functor (F: PAIR) -> struct
  let x = F.pair |> fst
end

module MakeSnd : MAKEPROJECTION = functor (F: PAIR) -> struct
  let x = F.pair |> snd
end

module Pair : PAIR = struct let pair = ( 21, 42 ) end
module Fst : VAL = MakeFst (Pair)
module Snd : VAL = MakeSnd (Pair)

let () = Printf.printf "Fst.x = %d, Snd.x = %d\n" Fst.x Snd.x
