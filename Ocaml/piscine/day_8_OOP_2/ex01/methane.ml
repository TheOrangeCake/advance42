(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   methane.ml                                         :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 00:40:06 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 00:40:06 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class methane =
  object (self)
  inherit Molecule.molecule "Methane" 
  [
    (new Carbon.carbon :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
  ]
end
