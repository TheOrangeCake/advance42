(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/31 18:58:16 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/01 00:06:55 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () =
  let sidekick = new People.people "Pookie" in
  let doc = new Doctor.doctor "Frimousse" 15 sidekick in
  print_endline doc#to_string;
  doc#talk;
  doc#travel_in_time 1996 2026;
  print_endline doc#to_string;
  doc#use_sonic_screwdriver
  (* doc#regenerate *)
