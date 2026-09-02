(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/31 18:58:16 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/02 15:10:10 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () =
  let human_names = ["human1"; "human2"; "human3"] in
  let doctor_names = ["doctor1"; "doctor2"; "doctor3"] in
  let sidekick_names = ["side1"; "side2"; "side3"] in

  let army_human = new Army.army in
  let army_doctor = new Army.army in
  let army_dalek = new Army.army in
  let len = List.length human_names in

  print_endline "\n===== RAISING THE ARMIES =====";
  let rec build i =
    if i >= len then ()
    else begin
      let human = new People.people (List.nth human_names i) in
      army_human#add human;

      let sidekick = new People.people (List.nth sidekick_names i) in
      army_doctor#add (new Doctor.doctor (List.nth doctor_names i) 15 sidekick);

      let dalek = new Dalek.dalek in
      army_dalek#add dalek;

      print_endline "";
      build (i + 1)
    end
  in build 0;

  print_endline "\n===== DISBANDING THE ARMIES =====";
  let rec destroy i =
    if i > len then ()
    else begin
      army_human#delete;
      army_doctor#delete;
      army_dalek#delete;
      destroy (i + 1)
    end
  in destroy 0
