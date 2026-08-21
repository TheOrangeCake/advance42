(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   jokes.ml                                           :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/21 00:30:24 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/21 14:15:18 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () = Random.self_init ()

(* Resource: https://www.w3tutorials.net/blog/how-do-i-read-in-lines-from-a-text-file-in-ocaml/ *)
let () =
  if Array.length Sys.argv <> 2
    then print_endline "Wrong number of argument"
  else
    try
      In_channel.with_open_text Sys.argv.(1) (fun ic ->
        let acc = ref [] in
        (
          try
            while true do
              acc := input_line ic :: !acc
            done
          with End_of_file -> ()
        );

        let jokes = Array.of_list !acc in
        let len = Array.length jokes in
        if len = 0
          then print_endline "No joke in file"
        else
          print_endline jokes.(len |> Random.int)
      )
    with Sys_error e -> print_endline e
