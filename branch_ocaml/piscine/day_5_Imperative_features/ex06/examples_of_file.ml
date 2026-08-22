(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   examples_of_file.ml                                :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/22 09:53:59 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/22 10:14:23 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let examples_of_file ?(path = "./ionosphere.train.csv") () =
  ()

let () =
  let len = Array.length Sys.argv in
  if len = 1
    then examples_of_file ()
  else if len = 2
    then examples_of_file ~path:Sys.argv.(1) ()
  else
    print_endline "Wrong number of argument"
