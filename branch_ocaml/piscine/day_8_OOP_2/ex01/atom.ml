(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   atom.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/02 16:41:29 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/02 22:27:37 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class virtual atom (name: string) (symbol: string) (atomic_number: int) = 
  object (self)
    method name = name
    method symbol = symbol
    method atomic_number = atomic_number
    method to_string =
      "Name: " ^ name ^
      " | Symbol: " ^ symbol ^
      " | Atomic number: " ^ string_of_int atomic_number
    method equals (other: atom) = atomic_number = other#atomic_number
end
