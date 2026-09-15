(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   alkane_combustion.ml                               :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/04 15:59:10 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/07 23:07:57 by hoannguy         ###   ########.fr       *)
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

    (* alkane + b O2 = c CO2 + e CO + f C + d H2O *)
    method get_incomplete_results : (int * (Molecule.molecule * int) list) list =
      let in_side = self#cal_atom start in                             
      let get sym = match List.assoc_opt sym in_side with
      | Some count -> count
      | None -> failwith "Invalid alkane"
      in
      let sumC = get "C" in
      let d = get "H" / 2 in
      let b_min = (d + 1) / 2 in (* min b = required to make H2O *)
      let b_max = (2 * sumC + d - 1) / 2 in (* max b = complete combustion *)
      let rec range lo hi =
        if lo > hi then []
        else lo :: range (lo + 1) hi
      in
      let splits b =
        let budget = 2 * b - d in (* Oxygen left after used for H2O *)
        let rec loop c acc =
          if c > sumC then List.rev acc (* if run out of Carbon *)
          else
            let e = budget - 2 * c in (* cal CO: Oxygen used to create CO2 *)
            let f = sumC - c - e in (* soot C: unused C so total C - C used in CO2 - C used in CO *)
            if e >= 0 && f >= 0 then loop (c + 1) ((c, e, f) :: acc)
              (* if e < 0 then created too much CO2 *)
              (* if f < 0 then used too much C *)
            else loop (c + 1) acc
        in loop 0 []
      in
      let build (c, e, f) : (Molecule.molecule * int) list =
        let add mol count lst = if count = 0 then lst else (mol, count) :: lst in
        let products =
          add (new Carbon_dioxide.carbon_dioxide) c
            (add (new Carbon_monoxide.carbon_monoxide) e
              (add (new Soot.soot) f
                (add (new Water.water) d [])))
        in
        List.sort (fun a b -> String.compare (fst a)#formula (fst b)#formula) products
      in
      List.concat_map
        (fun b -> List.map (fun s -> (b, build s)) (splits b)) (* b is nb of Oxygen *)
        (range b_min b_max) (* list of nb of Oxygen *)

    method is_balanced =
      let in_side = self#cal_atom start in
      let out_side = self#cal_atom result in
      in_side = out_side
end
