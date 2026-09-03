(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   reaction.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 23:07:39 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 23:35:44 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class virtual reaction
  (start: (Molecule.molecule * int) list) 
  (result: (Molecule.molecule * int) list) =
  object (self)
    val start = start
    val result = result
    method virtual get_start : (Molecule.molecule * int) list
    method virtual get_result : (Molecule.molecule * int) list
    method virtual balance : reaction
    method virtual is_balanced: bool
end
