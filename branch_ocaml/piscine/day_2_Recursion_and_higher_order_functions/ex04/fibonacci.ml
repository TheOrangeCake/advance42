(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   fibonacci.ml                                       :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:53:03 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:53:05 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let fibonacci n =
  if n < 0
    then -1
  else
    let rec fibo n prev1 prev2 =
      if n = 0 
        then prev1
      else if n = 1
        then prev2
      else
        fibo (n - 1) prev2 (prev1 + prev2)
      in fibo n 0 1
      

(* let () =
    assert (fibonacci (-42) = -1);
    assert (fibonacci 0 = 0);
    assert (fibonacci 1 = 1);
    assert (fibonacci 3 = 2);
    assert (fibonacci 6 = 8); *)
