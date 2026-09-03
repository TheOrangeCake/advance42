(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/02 16:41:42 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 23:03:45 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () =
  let methane = new Methane.methane in
  print_endline methane#to_string;

  let ethane = new Ethane.ethane in
  print_endline ethane#to_string;

  let octane = new Octane.octane in
  print_endline octane#to_string;

  let ethane2 = new Ethane.ethane in
  Printf.printf "Methane equals Octane -> %b\n" (methane#equals (octane :> Molecule.molecule));
  Printf.printf "Ethane equals Ethane -> %b\n" (ethane#equals ethane2)
