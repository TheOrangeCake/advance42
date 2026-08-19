(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   micronap.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/19 21:30:59 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/19 23:32:15 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

(* Compile: ocamlopt -I +unix unix.cmxa micronap.ml *)

let my_sleep () : unit = Unix.sleep 1

let main () =
  if Array.length Sys.argv <> 2
    then print_endline "Wrong number of argument"
  else
    try
      let second = int_of_string Sys.argv.(1) in
      if second >= 0
        then begin
          print_string "Gonna sleep for ";
          print_int second;
          print_endline " second(s)...";
          for _ = 1 to second do
            my_sleep ()
          done
        end
      else print_endline "Argument is negative"
    with Failure _ -> print_endline "Argument is not a number"

let () = main ()
