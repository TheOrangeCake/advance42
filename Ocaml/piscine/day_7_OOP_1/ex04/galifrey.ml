(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   galifrey.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/02 15:16:20 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/02 15:59:49 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let alive l = List.filter (fun x -> x#is_alive) l
let count l = List.length (alive l)
let pick l = List.nth l (Random.int (List.length l))

let max_rounds = 10

class galifrey
  (daleks: Dalek.dalek list)
  (doctors: Doctor.doctor list)
  (people: People.people list) =
  object (self)
    val members_dalek = daleks
    val members_doctor = doctors
    val members_people = people

    method to_string =
      "Daleks: " ^ string_of_int (count members_dalek) ^
      " | Doctors: " ^ string_of_int (count members_doctor) ^
      " | Humans: " ^ string_of_int (count members_people)

    method private dalek_turn (d: Dalek.dalek) =
      if d#is_alive then begin
        d#talk;
        match alive members_people, alive members_doctor with
        | [], [] -> ()
        | [], docs ->
          let target = pick docs in
          print_endline (d#name ^ " fires at " ^ target#name ^ "!");
          target#take_damage (10 + Random.int 30)
        | humans, _ ->
          let victim = pick humans in
          print_endline (d#name ^ " exterminates " ^ victim#name ^ "!");
          d#exterminate victim
      end

    method private doctor_turn (doc: Doctor.doctor) =
      if doc#is_alive then
        match alive members_dalek with
        | [] -> ()
        | daleks ->
          let target = pick daleks in
          doc#use_sonic_screwdriver;
          print_endline (doc#name ^ " attacks " ^ target#name ^ "!");
          target#take_damage (20 + Random.int 30)

    method private people_turn (p: People.people) =
      if p#is_alive then p#talk

    method private outcome =
      if count members_dalek = 0 then Some "The Daleks was defeated! Survivors rejoice!"
      else if count members_doctor = 0 && count members_people = 0 then
        Some "Om nom nom. The Daleks have won..."
      else None

    method do_time_war =
      let rec fight n =
        print_endline ("\n--- Round " ^ string_of_int n ^ " --- " ^ self#to_string);
        List.iter (fun d -> self#dalek_turn d) (alive members_dalek);
        List.iter (fun d -> self#doctor_turn d) (alive members_doctor);
        List.iter (fun p -> self#people_turn p) (alive members_people);
        match self#outcome with
        | Some msg -> print_endline ("\n" ^ msg)
        | None ->
          if n >= max_rounds then
            print_endline "\nThe Time War drags on with no winner in sight..."
          else fight (n + 1)
      in print_endline ("The Time War begins! " ^ self#to_string);
      fight 1
end
