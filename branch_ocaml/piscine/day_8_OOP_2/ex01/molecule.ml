(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   molecule.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/02 22:36:43 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 00:37:20 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class virtual molecule (name: string) (lst: Atom.atom list) =
  object (self)
    method name = name

    method formula =
      let sorted = List.sort (fun a b -> String.compare a#symbol b#symbol) lst
      in
      let assign_index = function
        | "C" -> 0
        | "H" -> 1
        | _ -> 2
      in
      let rec loop atom_lst count acc =
        match atom_lst with
        | [] -> List.rev acc
        | h :: [] -> List.rev ((assign_index h#symbol, h#symbol, count) :: acc)
        | h :: (next :: _ as t) ->
          if h#equals next then loop t (count + 1) acc
          else 
            loop t 1 ((assign_index h#symbol, h#symbol, count) :: acc)
      in let sorted2 = List.sort compare (loop sorted 1 []) in 
      String.concat "" (List.map (fun (_, b, c) -> if c = 1 then b else b ^ string_of_int c) sorted2)

    method to_string = "Name: " ^ self#name ^ " | Formula: " ^ self#formula
    method equals (other: molecule) = self#formula = other#formula
end
