(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   jokes.ml                                           :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/21 00:30:24 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/21 01:23:52 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () = Random.self_init ()

let () =
  let jokes = [|
    "Why don’t oysters donate to charity? Because they’re shellfish.";
    "What do you call a fish with no eye? Fsh.";
    "Why was 6 afraid of 7? Because 7 ate 9.";
    "Why do birds fly south for the winter? Because it’s too far to walk.";
    "What do you call a deer with no eyes? No eyed deer."
  |] in
  print_endline (jokes.(Array.length jokes |> Random.int))
