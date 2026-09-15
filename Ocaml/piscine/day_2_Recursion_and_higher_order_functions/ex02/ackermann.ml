(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ackermann.ml                                       :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:52:47 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:52:49 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let rec ackermann m n =
  if m < 0 || n < 0
    then -1
  else if m = 0
    then n + 1
  else if n = 0
    then ackermann (m - 1) 1
  else
    ackermann (m - 1) (ackermann m (n - 1))

let () =
        assert (ackermann 7 (-1) = -1);
        assert (ackermann (-1) 7 = -1);
        assert (ackermann 0 0 = 1);
        assert (ackermann 2 3 = 9);
        assert (ackermann 4 1 = 65533);
        (* Below will stack overflow, wait MINUTES *)
        (* print_int (ackermann 4 3); *)
