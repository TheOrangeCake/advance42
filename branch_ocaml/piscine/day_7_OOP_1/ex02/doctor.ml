(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   doctor.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/31 23:26:40 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/01 00:08:06 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class doctor (name: string) (age: int) (sidekick: People.people)=
  object (self)
  val name = name
  val mutable age = age
  val sidekick = sidekick
  val mutable hp = 100
  method to_string =
    "Name: " ^ name ^
    " | Age: " ^ string_of_int age ^
    " | Hp: " ^ string_of_int hp ^
    " | Sidekick: " ^ sidekick#to_string
  method talk = print_endline "Hi! I'm the Doctor!"
  initializer print_endline ("An esteemed doctor, " ^ name ^ ", has risen! " ^ self#to_string)

  (* https://www.asciiart.eu/art/e79441129573cee0 *)
  method travel_in_time (start: int) (arrival: int) =
    age <- age + abs (arrival - start);
    print_endline "                  _.--._
                    _|__|_
        _____________|__|_____________
      .-'______________________________'-.
      | |________POLICE___BOX__________| |
      |  |============================|  |
      |  | .-----------..-----------. |  |
      |  | |  _  _  _  ||  _  _  _  | |  |
      |  | | | || || | || | || || | | |  |
      |  | | |_||_||_| || |_||_||_| | |  |
      |  | | | || || | || | || || | | |  |
      |  | | |_||_||_| || |_||_||_| | |  |
      |  | |  _______  ||  _______  | |  |
      |  | | |       | || |       | | |  |
      |  | | |       | || |       | | |  |
      |  | | |       | || |       | | |  |
      |  | | |_______| || |_______| | |  |
      |  | |  _______ @||@ _______  | |  |
      |  | | |       | || |       | | |  |
      |  | | |       | || |       | | |  |
      |  | | |       | || |       | | |  |
      |  | | |_______| || |_______| | |  |
      |  | |  _______  ||  _______  | |  |
      |  | | |       | || |       | | |  |
      |  | | |       | || |       | | |  |
      |  | | |       | || |       | | |  |
      |  | | |_______| || |_______| | |  |
      |  | '-----------''-----------' |  |
    _|__|/__________________________\\|__|_ 
    '----'----------------------------'----'
    "

  method use_sonic_screwdriver = print_endline "Whiiiiwhiiiwhiii Whiiiiwhiiiwhiii Whiiiiwhiiiwhiii"
  method private regenerate = hp <- 100
end
