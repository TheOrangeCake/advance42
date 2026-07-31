(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_sum.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 14:10:23 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 15:05:09 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let ft_sum f lower upper =
  if upper < lower
    then nan
  else
    let acc = 0. in
    let rec sum low acc =
      if low > upper
        then acc
      else
        let result = ( +. ) acc (f low) in
        sum (low + 1) result
    in sum lower acc

let squr i = float_of_int (i * i)

let () =
        assert (Float.is_nan (ft_sum squr 2 1));
        assert (ft_sum squr 1 10 = 385.)
  