(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   encode.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/01 23:10:02 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/02 00:08:27 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let rec encode = function
| [] -> []
| x :: rest ->
    match encode rest with
    | (n, y) :: tail when x = y -> (n + 1, y) :: tail
    | acc -> (1, x) :: acc

let () =
  assert (encode [] = []);
  assert (encode ['a'; 'a'; 'a'] = [(3, 'a')]);
  assert (encode ['a'; 'a'; 'a'; 'b'; 'b'] = [(3, 'a'); (2, 'b')]);
  assert (encode ['a'; 'a'; 'b'; 'b'; 'a'] = [(2, 'a'); (2, 'b'); (1, 'a')]);
  assert (encode [1; 2; 3; 1; 1; 2] = [(1, 1); (1, 2); (1, 3); (2, 1); (1, 2)])
