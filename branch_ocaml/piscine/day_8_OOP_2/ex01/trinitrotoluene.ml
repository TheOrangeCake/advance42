(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   trinitrotoluene.ml                                 :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 00:26:27 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 00:30:29 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class trinitrotoluene =
  object (self)
  inherit Molecule.molecule "Trinitrotoluene" 
  [
    (new Nitrogen.nitrogen :> Atom.atom);
    (new Nitrogen.nitrogen :> Atom.atom);
    (new Nitrogen.nitrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
    (new Carbon.carbon :> Atom.atom);
    (new Carbon.carbon :> Atom.atom);
    (new Carbon.carbon :> Atom.atom);
    (new Carbon.carbon :> Atom.atom);
    (new Carbon.carbon :> Atom.atom);
    (new Carbon.carbon :> Atom.atom);
    (new Carbon.carbon :> Atom.atom);
  ]
end
