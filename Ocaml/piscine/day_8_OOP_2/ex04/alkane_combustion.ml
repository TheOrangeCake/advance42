(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   alkane_combustion.ml                               :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/04 15:59:10 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/07 00:01:45 by hoannguy         ###   ########.fr       *)
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

    method private cal_atom (mol_lst: (Molecule.molecule * int) list) : (string * int) list =
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

    (* a alkane + b O2 = c CO2 + d H2O *)
    method private balanced =
      if self#is_balanced then self
      else
        let in_side = self#cal_atom start in
        let sumC = match List.assoc_opt "C" in_side with
        | Some count -> count
        | None -> failwith "Invalid alkane"
        in
        let sumH = match List.assoc_opt "H" in_side with 
        | Some count -> count
        | None -> failwith "Invalid alkane"
        in
        let coeff = if (sumH / 2) mod 2 = 1 then 2 else 1 in
        let a = coeff * 1 in
        let c = coeff * sumC in
        let d = coeff * sumH / 2 in
        let b = (c * 2 + d) / 2 in
        let good_start =
          List.map (fun (m, _) ->
            if m#formula = "O2" then (m, b)
            else (m, a)) start
        in
        let good_result =
          List.map (fun (m, _) ->
            if m#formula = "CO2" then (m, c)
            else if m#formula = "H2O" then (m, d)
            else failwith "Equation wasn't initialized correctly"
            ) result
        in
        {< start = good_start; result = good_result >}

    method balance = (self#balanced :> Reaction.reaction)

    method is_balanced =
      let in_side = self#cal_atom start in
      let out_side = self#cal_atom result in
      in_side = out_side
end
