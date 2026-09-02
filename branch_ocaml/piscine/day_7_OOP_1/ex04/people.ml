(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   people.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/31 18:58:20 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/01 22:46:33 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class people (name: string) =
  object (self)
    val name = name
    val mutable hp = 100

    method name = name
    method hp = hp
    method is_alive = hp > 0

    method to_string =
      "Name: " ^ name ^
      " | Hp: " ^ string_of_int hp ^
      " | Status: " ^ if self#is_alive then "alive" else "dead"

    method talk = 
      if not self#is_alive then print_endline (name ^ " is already dead!")
      else print_endline ("I'm " ^ name ^ "! Do you know the Doctor?")

    method die =
      if not self#is_alive then print_endline (name ^ " is already dead!")
      else begin print_endline "Aaaarghh!"; hp <- 0 end
    
    initializer print_endline ("Me, " ^ name ^ ", has risen! " ^ self#to_string)

    method take_damage damage = 
      if damage < 0 then invalid_arg "Invalid damage point"
      else if not self#is_alive then print_endline (name ^ " is already dead")
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
