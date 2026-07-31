(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   hofstadter_mf.ml                                   :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:53:08 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:53:08 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let rec hfs_f n =
  if n = 0
    then 1
  else if n < 0
    then -1
  else
    n - hfs_m (hfs_f (n - 1))
and hfs_m n =
  if n = 0
    then 0
  else if n < 0
    then -1
  else
    n - hfs_f (hfs_m (n - 1))

(* let () =
    assert (hfs_m 0 = 0);
    assert (hfs_f 0 = 1);
    assert (hfs_m 4 = 2);
    assert (hfs_f 4 = 3); *)
