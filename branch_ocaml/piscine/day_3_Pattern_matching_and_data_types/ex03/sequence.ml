(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   sequence.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 17:40:09 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/02 22:30:01 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let string_of_digit = function
  | 1 -> "1"
  | 2 -> "2"
  | 3 -> "3"
  | 4 -> "4"
  | 5 -> "5"
  | 6 -> "6"
  | 7 -> "7"
  | 8 -> "8"
  | 9 -> "9"
  | 0 -> "0"
  | _ -> ""

let sequence n =
  if n <= 0
    then ""
  else
    let rec loop x lst =
      if x >= n
        then lst
      else
        let rec iter_list lst count =
          match lst with
          | [] -> []
          | [a] -> a :: count :: []
          | a :: (b :: _ as rest) ->
            if a = b
              then
                iter_list rest (count + 1)
              else
                a :: count :: iter_list rest 1

        in loop (x + 1) (iter_list lst 1)
      
      in let rec to_string lst acc =
        match lst with
        | [] -> acc
        | h :: t -> to_string t (string_of_digit h ^ acc)
        
      in to_string (loop 1 [1]) ""

let () =
    assert (sequence 1 = "1");
    assert (sequence 2 = "11");
    assert (sequence 3 = "21");
    assert (sequence 4 = "1211");
    assert (sequence 5 = "111221");
    assert (sequence 6 = "312211");
    assert (sequence 7 = "13112221");
    assert (sequence 8 = "1113213211");
    assert (sequence 0 = "");
    assert (sequence (-1) = "")
