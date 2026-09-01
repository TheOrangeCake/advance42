(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/31 18:58:16 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/01 23:00:57 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () =
  let human = new People.people "Moussy" in
  let sidekick = new People.people "Pookie" in
  let doc = new Doctor.doctor "Frimousse" 15 sidekick in
  let monster = new Dalek.dalek in

  print_endline "\n===== EVERYONE SPEAKS =====";
  human#talk;
  sidekick#talk;
  doc#talk;
  monster#talk;

  print_endline "\n===== THE DOCTOR ARRIVES =====";
  doc#travel_in_time 1986 2026;
  doc#use_sonic_screwdriver;
  print_endline doc#to_string;

  print_endline "\n===== THE DALEK EXTERMINATES THE HUMAN =====";
  monster#exterminate human;
  print_endline human#to_string;
  print_endline monster#to_string;
  human#talk;

  print_endline "\n===== DOCTOR VS DALEK =====";
  let rec battle round =
    if not monster#is_alive then print_endline "The Doctor wins!"
    else if not doc#is_alive then print_endline "The Dalek wins! EXTERMINATE!"
    else if round > 5 then print_endline "The Time War drags on..."
    else begin
      print_endline ("--- Round " ^ string_of_int round ^ " ---");
      if round = 3 
        then begin
          print_endline "Exterminate the sidekick!";
          monster#exterminate sidekick
        end;
      monster#take_damage 30;
      if monster#is_alive then begin
        monster#talk;
        doc#take_damage 15
      end;
      print_endline monster#to_string;
      print_endline doc#to_string;
      print_char '\n';
      battle (round + 1)
    end
  in battle 1
