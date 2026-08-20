(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   jokes.ml                                           :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/21 00:30:24 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/21 01:26:48 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () = Random.self_init ()

let () =
  if Array.length Sys.argv <> 2
    then print_endline "Wrong number of argument"
  else
    (* open file *)
    let jokes = [|
      (* placeholder *)
    |] in
    print_endline (jokes.(Array.length jokes |> Random.int))
