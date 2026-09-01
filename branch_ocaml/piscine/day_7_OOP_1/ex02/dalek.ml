(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   dalek.ml                                           :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/01 00:10:56 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/01 23:06:05 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () = Random.self_init ()

class dalek =
  object (self)
    val name =
      let r1 = Random.int 94 in
      let r2 = Random.int 94 in
      let r3 = Random.int 94 in
      "Dalek" ^ String.make 1 (char_of_int (r1 + 33))
      ^ String.make 1 (char_of_int (r2 + 33))
      ^ String.make 1 (char_of_int (r3 + 33))

    val mutable hp = 100
    val mutable shield = true

    (* extra *)
    method name = name
    method hp = hp
    method is_alive = hp > 0
    (* end extra *)

    method to_string =
      "Name: " ^ name ^
      " | Hp: " ^ string_of_int hp ^
      " | Shield: " ^ string_of_bool shield ^
      " | Status: " ^ if self#is_alive then "alive" else "dead"

    method talk =
      if not self#is_alive then print_endline (name ^ " is already dead")
      else
        let r = match Random.int 4 with
          | 0 -> "Explain! Explain!"
          | 1 -> "Exterminate! Exterminate!"
          | 2 -> "I obey!"
          | _ -> "You are the Doctor! You are the enemy of the Daleks!"
        in print_endline r
    
    method exterminate (victim: People.people) =
      if not self#is_alive then print_endline (name ^ " is already dead")
      else begin
        shield <- not shield;
        victim#die
      end
    
    method die = 
      if not self#is_alive then print_endline (name ^ " is already dead")
      else begin
        print_endline "Emergency Temporal Shift!";
        hp <- 0
      end

    (* extra *)    
    method take_damage damage = 
      if damage < 0 then invalid_arg "Invalid damage point"
      else if not self#is_alive then print_endline (name ^ " is already dead")
      else if shield then begin
          print_endline (name ^ " has shield, no damage done!");
          shield <- not shield
        end
      else
        let remain = hp - damage in
        if remain <= 0 then self#die
        else hp <- remain

    method heal point =
      if point < 0 then invalid_arg "Invalid heal point"
      else if not self#is_alive then print_endline (name ^ " is already dead")
      else
        let h = hp + point in
        hp <- if h > 100 then 100 else h
end
