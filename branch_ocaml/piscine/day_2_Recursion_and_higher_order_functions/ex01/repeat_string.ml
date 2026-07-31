(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   repeat_string.ml                                   :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:52:40 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:52:42 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let repeat_string ?(str="x") n =
  if n < 0
    then
      "Error"
    else
      let rec repeat str x acc =
        if x <= 0
          then
            acc
        else
          repeat str (x - 1) (acc ^ str)
      in repeat str n ""

(* let () =
      assert (repeat_string (-1) = "Error");
      assert (repeat_string 0 = "");
      assert (repeat_string ~str:"Toto" 1 = "Toto");
      assert (repeat_string 2 = "xx");
      assert (repeat_string ~str:"a" 5 = "aaaaa");
      assert (repeat_string ~str:"what" 3 = "whatwhatwhat");
      assert (repeat_string ~str:"a" 5 = "aaa"); *)
