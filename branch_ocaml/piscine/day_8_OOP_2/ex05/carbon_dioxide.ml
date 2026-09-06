(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   carbon_dioxide.ml                                  :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 00:26:27 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 00:31:26 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class carbon_dioxide =
  object (self)
  inherit Molecule.molecule "Carbon dioxide" 
  [
    (new Carbon.carbon :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
  ]
end
