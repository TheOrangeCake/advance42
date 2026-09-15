(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/02 16:41:42 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/07 22:40:46 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let with_coeff n s =
  if n = 1 then s else string_of_int n ^ s

let side_to_string lst =
  String.concat " + " (List.map (fun (m, n) -> with_coeff n m#formula) lst)

let print_complete (alk: Alkane.alkane) =
  let combustion = (new Alkane_combustion.alkane_combustion [alk])#balance in
  print_endline ("Complete   : " ^ side_to_string combustion#get_start
    ^ " -> " ^ side_to_string combustion#get_result)

let print_incomplete (alk: Alkane.alkane) =
  let combustion = new Alkane_combustion.alkane_combustion [alk] in
  let results = combustion#get_incomplete_results in
  print_endline ("=== " ^ alk#name ^ " (" ^ alk#formula ^ ") : "
    ^ string_of_int (List.length results) ^ " incomplete results ===");
  print_complete alk;
  List.iter (fun (o2, products) ->
    print_endline ("Incomplete : " ^ alk#formula ^ " + " ^ with_coeff o2 "O2"
      ^ " -> " ^ side_to_string products)) results;
  print_newline ()

let () =
  print_incomplete (new Methane.methane);
  print_incomplete (new Ethane.ethane);
  print_incomplete (new Propane.propane);
  print_incomplete (new Octane.octane)
