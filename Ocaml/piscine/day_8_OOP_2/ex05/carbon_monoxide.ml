(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   carbon_monoxide.ml                                 :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/06 23:45:00 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/06 23:45:00 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class carbon_monoxide =
  object (self)
  inherit Molecule.molecule "Carbon monoxide" 
  [
    (new Carbon.carbon :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
  ]
end
