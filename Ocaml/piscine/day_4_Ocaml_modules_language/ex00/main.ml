(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/07 12:02:49 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/07 12:56:49 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let main () =
  let all_type = Color.all in
  let rec print = function
    | [] -> ()
    | h :: t ->
        print_string (Color.toString h);
        print_string " -> ";
        print_endline (Color.toStringVerbose h);
        print t
  in print all_type

let () =
      main ()
