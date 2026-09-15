(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   alkane.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/03 22:01:50 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/07 00:05:40 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let get_name = function
| 1 -> "Methane"
| 2 -> "Ethane"
| 3 -> "Propane"
| 4 -> "Butane"
| 5 -> "Pentane"
| 6 -> "Hexane"
| 7 -> "Heptane"
| 8 -> "Octane"
| 9 -> "Nonane"
| 10 -> "Decane"
| 11 -> "Undecane"
| 12 -> "Dodecane"
| 13 -> "Tridecane"
| 14 -> "Tetradecane"
| 15 -> "Pentadecane"
| 16 -> "Hexadecane"
| 17 -> "Heptadecane"
| 18 -> "Octadecane"
| 19 -> "Nonadecane"
| 20 -> "Eicosane"
| _ -> invalid_arg "alkane: unsupported carbon count"

class virtual alkane (n: int) =
  let rec loopC nC =
    match nC with
    | 0 -> []
    | x -> (new Carbon.carbon :> Atom.atom) :: loopC (x - 1)
  in let rec loopH nH =
    match nH with
    | 0 -> []
    | x -> (new Hydrogen.hydrogen :> Atom.atom) :: loopH (x - 1)
  in
  let lst =  if n >= 0
    then loopC n @ loopH (2 * n + 2)
    else invalid_arg "Error: Negative n"
  in
  object (self)
    inherit Molecule.molecule (get_name n) lst
end
