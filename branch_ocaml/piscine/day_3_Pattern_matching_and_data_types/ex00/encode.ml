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

let print_quoted_char c =
  print_char '\''; print_char c; print_char '\''

let print_encoded print_elem lst =
  let rec print = function
    | [] -> ()
    | [(n, x)] -> print_string "("; print_int n; print_string ", ";
                  print_elem x; print_string ")"
    | (n, x) :: t -> print_string "("; print_int n; print_string ", ";
                     print_elem x; print_string "); "; print t
  in print_string "["; print lst; print_endline "]"

let () =
  assert (encode [] = []);
  assert (encode ['a'; 'a'; 'a'] = [(3, 'a')]);
  assert (encode ['a'; 'a'; 'a'; 'b'; 'b'] = [(3, 'a'); (2, 'b')]);
  assert (encode ['a'; 'a'; 'b'; 'b'; 'a'] = [(2, 'a'); (2, 'b'); (1, 'a')]);
  assert (encode [1; 2; 3; 1; 1; 2] = [(1, 1); (1, 2); (1, 3); (2, 1); (1, 2)]);

  print_string "encode []                        = ";
  print_encoded print_quoted_char (encode []);
  print_string "encode ['a'; 'a'; 'a']           = ";
  print_encoded print_quoted_char (encode ['a'; 'a'; 'a']);
  print_string "encode ['a'; 'a'; 'a'; 'b'; 'b'] = ";
  print_encoded print_quoted_char (encode ['a'; 'a'; 'a'; 'b'; 'b']);
  print_string "encode ['a'; 'a'; 'b'; 'b'; 'a'] = ";
  print_encoded print_quoted_char (encode ['a'; 'a'; 'b'; 'b'; 'a']);
  print_string "encode [1; 2; 3; 1; 1; 2]        = ";
  print_encoded print_int (encode [1; 2; 3; 1; 1; 2])
