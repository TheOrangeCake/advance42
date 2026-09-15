(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   water.ml                                           :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 00:26:27 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 00:30:24 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class water =
  object (self)
  inherit Molecule.molecule "Water" 
  [
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
  ]
end
