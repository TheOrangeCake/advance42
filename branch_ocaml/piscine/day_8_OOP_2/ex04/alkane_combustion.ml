(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   alkane_combustion.ml                               :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/04 15:59:10 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/06 19:56:34 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class alkane_combustion (lst: Alkane.alkane list) =
  let l_in = 
    let mol_lst = List.map (fun x -> (x :> Molecule.molecule)) lst in
    let mol_lst_uniq = List.sort_uniq (fun a b -> String.compare a#formula b#formula) mol_lst in
    mol_lst_uniq @ [new Dioxygen.dioxygen]
  in
  let l_out = [new Carbon_dioxide.carbon_dioxide; new Water.water]
  in
  object (self)
    inherit Reaction.reaction l_in l_out

    method get_start =
      if self#is_balanced then start
      else raise (Reaction.Unbalanced "Error: Unbalanced reaction")
    method get_result =
      if self#is_balanced then result
      else raise (Reaction.Unbalanced "Error: Unbalanced reaction")

    method balance = (self :> Reaction.reaction) (* Placeholder *)

    method is_balanced =
      let cal (mol_lst: (Molecule.molecule * int) list) =
        let expanded = List.concat_map (
            fun x -> let atom = (fst x)#atoms in
            List.map (fun (name, count) -> (name, count * snd x)) atom
          ) mol_lst in
        let sorted = List.sort (fun a b -> String.compare (fst a) (fst b)) expanded in
        let rec merge atom_lst sum acc =
          match atom_lst with
          | [] -> List.rev acc
          | (name, count) :: [] -> List.rev ((name, count + sum) :: acc)
          | (name1, count1) :: ((name2, _) :: _ as t) ->
              if name1 = name2 then merge t (sum + count1) acc
              else merge t 0 ((name1, sum + count1) :: acc)
        in merge sorted 0 []
      in
      let in_side = cal start in
      let out_side = cal result in
      in_side = out_side
end
