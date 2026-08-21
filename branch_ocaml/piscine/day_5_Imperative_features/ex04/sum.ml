(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   sum.ml                                             :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/21 14:16:17 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/21 14:33:25 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let sum (a: float) (b: float) = a +. b

let () =
  print_string "CASE 1 -> 1.0 + 2.0 = ";
  print_float (sum 1.0 2.0);
  print_char '\n';
  
  print_string "CASE 2 -> 1.5 + 2.3 = ";
  print_float (sum 1.5 2.3);
  print_char '\n';

  print_string "CASE 3 -> 0.5 + -2.0 = ";
  print_float (sum 0.5 (-2.0));
  print_char '\n';

  print_string "CASE 4 -> -1.5 + -2.5 = ";
  print_float (sum (-1.5) (-2.5));
  print_char '\n';

  print_string "CASE 5 -> 0.0 + 0.0 = ";
  print_float (sum 0.0 0.0);
  print_char '\n';

  print_string "CASE 6 -> nan + 1.0 = ";
  print_float (sum nan 1.0);
  print_char '\n';

  print_string "CASE 7 -> max_float + max_float = ";
  print_float (sum max_float max_float);
  print_char '\n'
