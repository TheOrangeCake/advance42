(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   app.ml                                             :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/08 21:04:08 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/10 20:47:33 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

type project = string * string * int

let zero : project = ("", "", 0)

let combine ((n1, s1, g1): project) ((n2, s2, g2): project) : project =
  let is_zero n s g =
    n = "" && s = "" && g = 0
  in
  if is_zero n1 s1 g1 then (n2, s2, g2)
  else if is_zero n2 s2 g2 then (n1, s1, g1)
  else
    let avg =  (g1 + g2) / 2 in
    (* > or >= depend on subject interpretation here *)
    let status = if avg > 80 then "succeed" else "failed" in
    (n1 ^ n2, status, avg)

let fail ((n, _, _): project) : project =
  (n, "failed", 0)

let success ((n, _, _): project) : project =
  (n, "succeed", 80)
