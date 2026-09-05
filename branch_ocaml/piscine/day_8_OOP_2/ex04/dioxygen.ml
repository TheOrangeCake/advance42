(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   dioxygen.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 00:26:27 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/04 18:54:27 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class dioxygen =
  object (self)
  inherit Molecule.molecule "Dioxygen" 
  [
    (new Oxygen.oxygen :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
  ]
end
