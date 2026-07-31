(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_print_rev.ml                                    :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/07/31 12:56:31 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/07/31 12:56:32 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let ft_print_rev str =
  let len = String.length str in
  let rec rev i =
    if i >= 0
      then (
        print_char(String.get str i);
        rev (i - 1)
      )
  in rev (len - 1);
  print_char '\n'

(* let () =
  ft_print_rev "Hello world !";
  ft_print_rev "Evaluation";
  ft_print_rev "From 42 Lausanne";
  ft_print_rev "" *)
