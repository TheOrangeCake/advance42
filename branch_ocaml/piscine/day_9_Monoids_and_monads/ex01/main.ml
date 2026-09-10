(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/08 09:04:51 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/10 22:01:58 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let print_proj ((n, s, g): App.project) =
  Printf.printf "Project %s has grade %d and is %s\n" 
  (if n = "" then "no name" else n)
  g
  (if s = "" then "undefined" else s)

let check2 (n1, s1, g1) (n2, s2, g2) f (e1, e2, e3) =
  let (n, s, g) = f (n1, s1, g1) (n2, s2, g2) in
  Printf.printf 
  "Combine (%s, %s, %d) (%s, %s, %d) -> (%s, %s, %d) | Expect: (%s, %s, %d)\n"
  n1 s1 g1 n2 s2 g2 n s g e1 e2 e3

let () =
  print_endline "-- base: normal, zero, fail, success --";
  let project1 = ("Math", "succeed", 90) in
  print_proj project1;
  let project2 = App.zero in
  print_proj project2;
  print_proj (App.fail project1);
  print_proj (App.success project1);

  print_endline "";
  print_endline "-- combine: zero --";
  let project3 = ("Philo", "failed", 42) in
  print_proj project3;
  check2 project3 App.zero App.combine project3;
  check2 App.zero project3 App.combine project3;
  
  print_endline "";
  print_endline "-- combine: normal --";
  let project4 = ("Social", "succeed", 95) in
  print_proj project4;
  check2 project3 project4 App.combine ("PhiloSocial", "failed", 68);
  check2 project4 project3 App.combine ("SocialPhilo", "failed", 68);

  (* Note here: The rule of associativity is not possible to implement
     while respecting the subject (name concatenate and string * string * int shape) *)
  (* print_endline "";
  print_endline "-- combine: associativity --";
  let res1 = App.combine (App.combine project3 project4) project1 in
  print_proj res1;
  let res2 = App.combine project3 (App.combine project4 project1)  in
  print_proj res2 *)
