(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   sequence.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 17:40:09 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/02 18:58:58 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let sequence n =
  if n <= 0
    then ""
  else
    let rec loop x lst =
      if x > n
        then lst
      else
        let rec iter_list lst count acc =
          match lst with
          | [] -> loop (x + 1) acc
          | [a] -> count :: a :: acc
          | a :: (b :: _ as rest) ->
            if a = b
              then
                iter_list rest (count + 1) acc
              else
                iter_list rest 1 (count :: a :: acc)

        in iter_list lst 1 []

      in loop 1 [1]

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
