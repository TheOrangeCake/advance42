(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   soot.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/06 23:45:00 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/06 23:45:00 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class soot =
  object (self)
  inherit Molecule.molecule "Soot" 
  [
    (new Carbon.carbon :> Atom.atom);
  ]
end
