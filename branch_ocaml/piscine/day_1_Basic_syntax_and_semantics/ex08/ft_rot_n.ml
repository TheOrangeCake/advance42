(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_rot_n.ml                                        :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:56:45 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:56:56 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let ft_rot_n n str =
  let rot base c = char_of_int (base + (int_of_char c - base + n) mod 26) in
  let transform c =
    if c >= 'a' && c <= 'z'
      then rot (int_of_char 'a') c
    else if c >= 'A' && c <= 'Z'
      then rot (int_of_char 'A') c
    else c
  in String.map transform str

(* let () =
      assert (ft_rot_n 1 "abcdefghijklmnopqrstuvwxyz" = "bcdefghijklmnopqrstuvwxyza");
      assert (ft_rot_n 13 "abcdefghijklmnopqrstuvwxyz" = "nopqrstuvwxyzabcdefghijklm");
      assert (ft_rot_n 42 "0123456789" = "0123456789");
      assert (ft_rot_n 2 "OI2EAS67B9" = "QK2GCU67D9");
      assert (ft_rot_n 0 "Damned !" = "Damned !");
      assert (ft_rot_n 42 "" = "");
      assert (ft_rot_n 1 "NBzlk qnbjr !" = "OCaml rocks !") *)
