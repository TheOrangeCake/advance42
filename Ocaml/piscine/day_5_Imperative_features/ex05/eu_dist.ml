(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   eu_dist.ml                                         :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/21 14:35:08 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/21 15:15:08 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let f pa pb = (pa -. pb) ** 2. 

let eu_dist (a: float array) (b: float array) =
  let len_a = Array.length a in
  let len_b = Array.length b in
  if len_a <= 0 || len_b <= 0
    then 0.0
  else
    let acc = ref 0. in

    let len =
      if len_a < len_b
        then len_a
      else len_b
    in
    
    for i = 0 to len - 1 do
      acc := !acc +. f a.(i) b.(i)
    done;
    sqrt !acc

let test label expected a b =
  print_string label;
  print_string " -> ";
  print_float (eu_dist a b);
  print_string " (expected ";
  print_float expected;
  print_string ")\n"

let () =
  test "CASE 1: 3-4-5 triangle       " 5.0 [|0.0; 0.0|] [|3.0; 4.0|];
  test "CASE 2: same point           " 0.0 [|1.2; 2.2; 3.2|] [|1.2; 2.2; 3.2|];
  test "CASE 3: one dimension        " 7.5 [|0.0|] [|-7.5|];
  test "CASE 4: four dimensions      " 2.0 [|1.0; 1.0; 1.0; 1.0|] [|0.0; 0.0; 0.0; 0.0|];
  test "CASE 5: negative coordinates " 5.0 [|-1.0; -2.0|] [|2.0; 2.0|];
  test "CASE 6: decimals             " 0.5 [|0.1; 0.2|] [|0.4; 0.6|];
  test "CASE 7: bigger vectors       " 8.0 [|1.0; 2.0; 3.0; 4.0|] [|5.0; 6.0; 7.0; 8.0|];
  test "CASE 8: symmetry with case 1 " 5.0 [|3.0; 4.0|] [|0.0; 0.0|]
