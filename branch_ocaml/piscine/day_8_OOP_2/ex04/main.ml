(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/02 16:41:42 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/06 23:42:04 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let side (l: (Molecule.molecule * int) list) =
  String.concat " + " (List.map
    (fun (m, n) -> (if n = 1 then "" else string_of_int n ^ " ") ^ m#formula) l)

let show (r: Reaction.reaction) =
  print_endline (side r#get_start ^ " -> " ^ side r#get_result)

let test name lst =
  Printf.printf "%-18s: " name;
  let raw = new Alkane_combustion.alkane_combustion lst in
  try
    let r = raw#balance in
    if r#is_balanced then show r
    else print_endline "KO: balance returned an unbalanced reaction"
  with Reaction.Unbalanced msg -> print_endline ("KO: " ^ msg)

let methane () = (new Methane.methane :> Alkane.alkane)
let ethane () = (new Ethane.ethane :> Alkane.alkane)
let octane () = (new Octane.octane :> Alkane.alkane)

let () =
  test "methane" [methane ()];
  test "ethane" [ethane ()];
  test "octane" [octane ()];
  test "methane + ethane" [methane (); ethane ()];
  test "methane + octane" [methane (); octane ()];
  test "ethane + octane" [ethane (); octane ()];
  test "octane twice" [octane (); octane ()];
  test "all three" [methane (); ethane (); octane ()];
  print_newline ();

  print_string "unbalanced raises -> ";
  let raw = new Alkane_combustion.alkane_combustion [octane ()] in
  (try ignore raw#get_start; print_endline "No exception"
   with Reaction.Unbalanced msg -> print_endline (msg))
