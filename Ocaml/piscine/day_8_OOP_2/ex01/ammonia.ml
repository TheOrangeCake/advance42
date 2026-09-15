(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ammonia.ml                                         :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 00:40:06 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 00:40:06 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class ammonia =
  object (self)
  inherit Molecule.molecule "Ammonia" 
  [
    (new Nitrogen.nitrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
  ]
end
