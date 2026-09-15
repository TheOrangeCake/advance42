(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   watchtower.ml                                      :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/08 08:09:51 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/08 20:47:11 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

type hour = int
let zero : hour = 12

let add (h1: hour) (h2: hour) : hour = 
  if h1 <= 0 || h2 <= 0 then invalid_arg "Error: Bad hour"
  else let res = (h1 + h2) mod zero in
    if res = 0 then zero
    else res
    
let sub (h1: hour) (h2: hour) : hour =
  if h1 <= 0 || h2 <= 0 then invalid_arg "Error: Bad hour"
  else
    let res = (h1 - h2) mod zero in
    if res <= 0 then res + zero
    else res
