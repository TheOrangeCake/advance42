(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   crossover.ml                                       :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 08:44:37 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/02 11:03:32 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let crossover lst1 lst2 =
  let rec is_mem x lst =
    match lst with
    | [] -> false
    | y :: tail -> x = y || is_mem x tail
  
  in let rec rev lst acc =
    match lst with
    | [] -> acc
    | h :: t -> rev t (h :: acc)

  in let rec loop lst1 acc =
    match lst1 with
    | [] -> rev acc []
    | x :: rest ->
        if is_mem x lst2 && not (is_mem x acc)
          then
            loop rest (x :: acc)
          else
            loop rest acc

  in loop lst1 []

let print_list print_elem lst =
  let rec print = function
    | [] -> ()
    | [x] -> print_elem x
    | x :: t -> print_elem x; print_string "; "; print t
  in print_string "["; print lst; print_endline "]"

let print_quoted s =
  print_string "\""; print_string s; print_string "\""

let () =
        assert (crossover [] [1; 2] = []);
        assert (crossover [1; 2] [] = []);
        assert (crossover [1; 2] [1; 3] = [1]);
        assert (crossover [1; 2; 3; 3] [1; 3; 1; 2] = [1; 2; 3]);
        assert (crossover ["42"; "Lausanne"; "42"; "Lausanne"] ["42"; "Lausanne"; "42"; "!"] = ["42"; "Lausanne"]);

        print_string "crossover [] [1; 2]                 = ";
        print_list print_int (crossover [] [1; 2]);
        print_string "crossover [1; 2] []                 = ";
        print_list print_int (crossover [1; 2] []);
        print_string "crossover [1; 2] [1; 3]             = ";
        print_list print_int (crossover [1; 2] [1; 3]);
        print_string "crossover [1; 2; 3; 3] [1; 3; 1; 2] = ";
        print_list print_int (crossover [1; 2; 3; 3] [1; 3; 1; 2]);
        print_string "crossover [\"42\"; \"Lausanne\"; \"42\"; \"Lausanne\"] [\"42\"; \"Lausanne\"; \"42\"; \"!\"] = ";
        print_list print_quoted (crossover ["42"; "Lausanne"; "42"; "Lausanne"] ["42"; "Lausanne"; "42"; "!"])
 