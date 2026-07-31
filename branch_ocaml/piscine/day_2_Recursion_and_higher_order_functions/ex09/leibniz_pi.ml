(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   leibniz_pi.ml                                      :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 15:09:12 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 16:46:58 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let f i =
  let sign =
    if i mod 2 = 0
      then 1.0
      else -1.0
  in sign /. float_of_int (2 * i + 1)

let leibniz_pi delta =
  if delta < 0.
    then -1
  else
    let reference = 4. *. atan 1. in
    let acc = 0. in
    let rec leibniz i acc =
      let diff = reference -. (4. *. acc) in
      let gap =
        if diff < 0.
          then diff *. -1.
        else diff
      in if gap <= delta
        then i
      else
        leibniz (i + 1) (acc +. f i)
    in leibniz 0 acc

(* let approx n =
  let acc = 0. in
  let rec go i acc =
    if i >= n
      then 4. *. acc
    else
      go (i + 1) (acc +. f i)
  in go 0 acc

let dist n =
  let d = approx n -. (4. *. atan 1.) in
  if d < 0.
    then -. d
  else d

let check delta =
  let step = leibniz_pi delta in
  assert (dist step <= delta);
  assert (step = 0 || dist (step - 1) > delta)

let () =
  assert (leibniz_pi (-1.) = -1);
  assert (leibniz_pi (-0.0001) = -1);
  assert (leibniz_pi 4. = 0);
  check 1.;
  check 0.5;
  check 0.1;
  check 0.01;
  check 0.001 *)
