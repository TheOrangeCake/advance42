(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   examples_of_file.ml                                :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/22 09:53:59 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/23 00:16:41 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let examples_of_file (path: string) : (float array * string) list =
  try
    In_channel.with_open_text path (fun ic ->
        let acc = ref [] in
        (
          try
            while true do
              acc := input_line ic :: !acc
            done
          with End_of_file -> ()
        );
        
        let list_len = List.length !acc in
        if list_len < 2
          then failwith "Empty file or not enough columns"
          else
            let lst = ref [] in
            for i = 0 to list_len - 1 do
              let splitted = String.split_on_char ',' (List.nth !acc i) in
              let nb_of_float = (List.length splitted) - 1 in
              let f_array = Array.create_float nb_of_float in
              for idx = 0 to nb_of_float - 1 do
                Array.set f_array idx (List.nth splitted idx |> float_of_string)
              done;
              lst := (f_array, (List.nth splitted (List.length splitted - 1))) :: !lst
            done;
            !lst
      )
  with
  | Sys_error e | Failure e -> print_endline ("Bad csv: " ^ e); []


let rec examplesToString (e : (float array * string) list) =
  match e with
  | [] -> ()
  | h :: t ->
    match h with
    | (a, s) -> begin
      Array.iter (fun v -> print_float v; print_char ' ') a;
      print_endline s
    end;
    examplesToString t
  
let () = examples_of_file "./ionosphere.train.csv" |> examplesToString
