(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   reaction.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 23:07:39 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/05 22:17:24 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

exception Unbalanced of string

class virtual reaction
  (l_in: Molecule.molecule list)
  (l_out: Molecule.molecule list) =
  let tally molecule_lst =
    let sorted = List.sort (fun a b -> compare a#formula b#formula) molecule_lst
    in
    let rec loop sorted_lst count acc =
      match sorted_lst with
      | [] -> List.rev acc
      | h :: [] -> List.rev ((h, count) :: acc)
      | h :: (next :: _ as t) ->
        if h#equals next then loop t (count + 1) acc
        else 
          loop t 1 ((h, count) :: acc)
    in loop sorted 1 []
  in
  let l_start = tally l_in in
  let l_result = tally l_out
  in  
  object (self)
    val start = l_start
    val result = l_result
    method virtual get_start : (Molecule.molecule * int) list
    method virtual get_result : (Molecule.molecule * int) list
    method virtual balance : reaction
    method virtual is_balanced: bool
end
