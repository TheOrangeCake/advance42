(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/31 18:58:16 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/02 16:11:54 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () =
  print_endline "===== THE ARMIES OF THE TIME WAR =====";
  let people = List.map (fun n -> new People.people n) ["human1"; "human2"; "human3"] in
  let doctors = List.map2 (fun n s -> new Doctor.doctor n 900 s)
      ["doctor1"; "doctor2"; "doctor3"] [List.nth people 0; List.nth people 1; List.nth people 2] in
  let daleks = List.map (fun _ -> new Dalek.dalek) [1; 2; 3; 4] in

  let galifrey = new Galifrey.galifrey daleks doctors people in
  galifrey#do_time_war;

  print_endline "\n===== THE FINAL COUNT =====";
  List.iter (fun p -> print_endline p#to_string) people;
  List.iter (fun d -> print_endline d#to_string) doctors;
  List.iter (fun d -> print_endline d#to_string) daleks
