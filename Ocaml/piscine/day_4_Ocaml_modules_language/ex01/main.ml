(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/07 18:48:15 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/07 23:16:57 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let main () =
  let all_values = Value.all in
  let rec print = function
    | [] -> ()
    | h :: t ->
        print_string "Str: ";
        print_string (Value.toString h);
        print_string " | Int: ";
        print_int (Value.toInt h);
        print_string " | Svb: ";
        print_string (Value.toStringVerbose h);
        begin
          print_string " | next -> ";
          match Value.next h with
          | v -> print_string (Value.toString v)
          | exception (Invalid_argument e) -> print_string e
        end;
        begin
          print_string " | prev -> ";
          match Value.previous h with
          | v -> print_string (Value.toString v)
          | exception (Invalid_argument e) -> print_string e
        end;
        print_char '\n';
        print t
  in print all_values

let () =
  main ()
