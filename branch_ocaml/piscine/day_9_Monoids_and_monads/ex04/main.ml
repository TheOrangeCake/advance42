(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/13 18:00:30 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/14 23:48:07 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let print_set (label: string) (s: 'a Set.t) (f: 'a -> string) : unit =
  Printf.printf "%-30s { " label;
  Set.foreach s (fun x -> print_string (f x ^ " "));
  print_endline "}"

let pb (label: string) (v: bool) : unit = Printf.printf "%-28s %b\n" label v

let () =
  let a = Set.of_list [3; 1; 2; 3] in
  let b = Set.of_list [2; 3; 4] in
  let si = string_of_int in

  print_endline "--- build ---";
  print_set "of_list a [3;1;2;3]" a si;
  print_set "of_list b [2;3;4]" b si;
  print_set "return 42" (Set.return 42) si;
  print_set "zero" Set.zero si;
  print_set "of_list []" (Set.of_list []) si;

  print_endline "\n--- union - inter - diff ---";
  print_set "union a b" (Set.union a b) si;
  print_set "inter a b" (Set.inter a b) si;
  print_set "diff a b" (Set.diff a b) si;
  print_set "diff b a" (Set.diff b a) si;
  print_set "diff a a" (Set.diff a a) si;

  print_endline "\n--- monoid: (union, zero) ---";
  print_set "union a zero" (Set.union a Set.zero) si;
  print_set "  equal" a si;

  print_endline "\n--- monad: (bind, return) ---";
  print_set "bind a (x -> {x; x*10})" (Set.bind a (fun x -> Set.of_list [x; x * 10])) si;
  print_set "bind a return" (Set.bind a Set.return) si;
  print_set "  equal" a si;
  print_set "bind (return 3) (x->{x;2x})"
    (Set.bind (Set.return 3) (fun x -> Set.of_list [x; x * 2])) si;

  print_endline "\n--- filter - for_all - exists ---";
  print_set "filter a even" (Set.filter a (fun x -> x mod 2 = 0)) si;
  pb "for_all a (< 10)" (Set.for_all a (fun x -> x < 10));
  pb "for_all a even" (Set.for_all a (fun x -> x mod 2 = 0));
  pb "exists a (= 2)" (Set.exists a (fun x -> x = 2));
  pb "exists a (= 99)" (Set.exists a (fun x -> x = 99));

  print_endline "\n--- foreach ---";
  print_string "each element of a: ";
  Set.foreach a (fun x -> print_string ("[" ^ si x ^ "]"));
  print_newline ()

(* let () = Set.foreach (Set.inter [1;1;2] [1;2]) print_int *)
