(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   people.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/31 18:58:20 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/31 23:24:28 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class people (name: string) =
  object (self)
  val hp = 100
  method to_string = "Name: " ^ name ^ " | Hp: " ^ string_of_int hp
  method talk = print_endline ("I'm " ^ name ^ "! Do you know the Doctor?")
  method die = print_endline "Aaaarghh!"
  initializer print_endline ("Me, " ^ name ^ ", has risen! " ^ self#to_string)
end
