(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   alkane_combustion.ml                               :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/04 15:59:10 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/05 22:17:21 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class alkane_combustion (lst: Alkane.alkane list) =
  let l_in = 
    let mol_lst = List.map (fun x -> (x :> Molecule.molecule)) lst in
    mol_lst @ [new Dioxygen.dioxygen]
  in
  let l_out = [new Carbon_dioxide.carbon_dioxide; new Water.water]
  in
  object (self)
    inherit Reaction.reaction l_in l_out
    method get_start = if self#is_balanced then start else raise (Reaction.Unbalanced "Error: Unbalanced reaction")
    method get_result = if self#is_balanced then result else raise (Reaction.Unbalanced "Error: Unbalanced reaction")
    method balance = (self :> Reaction.reaction) (* Placeholder *)
    method is_balanced = false (* Place holder *)
    
end
