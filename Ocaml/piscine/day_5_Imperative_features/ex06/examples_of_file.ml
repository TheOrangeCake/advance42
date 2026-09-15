(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   examples_of_file.ml                                :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/22 09:53:59 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/23 09:46:19 by hoannguy         ###   ########.fr       *)
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
        if list_len = 0
          then failwith "Empty file"
        else
          let lst = ref [] in
          List.iter (fun line ->
            let fields = Array.of_list (String.split_on_char ',' line) in
            if Array.length fields < 2
              then failwith "Need at least 1 float column and 1 string column at the end"
            else
              let nb_of_float = (Array.length fields) - 1 in
              let f_array = Array.create_float nb_of_float in
              for idx = 0 to nb_of_float - 1 do
                f_array.(idx) <- (fields.(idx) |> float_of_string)
              done;
              lst := (f_array, fields.(nb_of_float)) :: !lst
          ) !acc;
          !lst
      )
  with
  | Sys_error e | Failure e -> print_endline ("Bad csv: " ^ e); []


let rec printExamples (e : (float array * string) list) =
  match e with
  | [] -> ()
  | (a, s) :: t ->
    print_string "[| ";
    Array.iter (fun v -> 
      print_float v;
      print_char ' ') a;
    print_string "|] ";
    print_endline s;
    printExamples t
  
let () =
  let path =
    if Array.length Sys.argv > 1
      then Sys.argv.(1)
    else "./ionosphere.train.csv"
  in examples_of_file path |> printExamples
