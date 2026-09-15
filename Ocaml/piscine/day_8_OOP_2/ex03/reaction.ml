(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   reaction.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 23:07:39 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/04 17:02:21 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class virtual reaction
  (l_in: Molecule.molecule list) 
  (l_out: Molecule.molecule list) =
  object (self)
    val virtual start : (Molecule.molecule * int) list
    val virtual result: (Molecule.molecule * int) list
    method virtual get_start : (Molecule.molecule * int) list
    method virtual get_result : (Molecule.molecule * int) list
    method virtual balance : reaction
    method virtual is_balanced: bool
end
