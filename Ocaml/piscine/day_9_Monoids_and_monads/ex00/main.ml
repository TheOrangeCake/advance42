(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/08 08:18:39 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/08 21:00:04 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

(*  Lesson note:
    What is a monoid?
    * There are 3 elements bundled together:
      - a type t for a set of values
      - a function t -> t -> t, so return same type to allow chaining
      - an identity element zero
    * There are 2 laws:
      - Associativity: grouping order doesn't matter f (f a b) c = f a (f b c)
      - Identity: f zero a = a and f a zero = a
*)
let check (label: string) (expected: Watchtower.hour) (f: unit -> Watchtower.hour) =
  try
    let got = f () in
    Printf.printf "%-26s -> %-3d (expected %-3d) %s\n"
      label got expected (if got = expected then "OK" else "KO")
  with Invalid_argument msg ->
    Printf.printf "%-26s -> raised Invalid_argument: %s\n" label msg

let () =
  Printf.printf "zero -> %d\n\n" Watchtower.zero;

  print_endline "-- add: plain hours --";
  check "add 2 3" 5 (fun () -> Watchtower.add 2 3);
  check "add 12 7" 7 (fun () -> Watchtower.add 12 7);
  check "add 15 12" 3 (fun () -> Watchtower.add 15 12);

  print_endline "\n-- add: wrapping onto 12 --";
  check "add 5 7" 12 (fun () -> Watchtower.add 5 7);
  check "add 6 6" 12 (fun () -> Watchtower.add 6 6);
  check "add 24 24" 12 (fun () -> Watchtower.add 24 24);

  print_endline "\n-- add: zero is the identity --";
  check "add 3 zero" 3 (fun () -> Watchtower.add 3 Watchtower.zero);
  check "add zero 3" 3 (fun () -> Watchtower.add Watchtower.zero 3);
  check "add zero zero" 12 (fun () -> Watchtower.add Watchtower.zero Watchtower.zero);

  print_endline "\n-- add: associativity --";
  check "add (add 5 7) 3" 3 (fun () -> Watchtower.add (Watchtower.add 5 7) 3);
  check "add 5 (add 7 3)" 3 (fun () -> Watchtower.add 5 (Watchtower.add 7 3));
  check "add (add 4 4) 4" 12 (fun () -> Watchtower.add (Watchtower.add 4 4) 4);
  check "add 4 (add 4 4)" 12 (fun () -> Watchtower.add 4 (Watchtower.add 4 4));

  print_endline "\n-- add: invalid hours --";
  check "add 0 3" 0 (fun () -> Watchtower.add 0 3);
  check "add 3 (-1)" 0 (fun () -> Watchtower.add 3 (-1));

  print_endline "\n-- sub: no wrap --";
  check "sub 12 7" 5 (fun () -> Watchtower.sub 12 7);
  check "sub 9 4" 5 (fun () -> Watchtower.sub 9 4);

  print_endline "\n-- sub: wrapping past 12 --";
  check "sub 7 9" 10 (fun () -> Watchtower.sub 7 9);
  check "sub 3 5" 10 (fun () -> Watchtower.sub 3 5);
  check "sub 2 15" 11 (fun () -> Watchtower.sub 2 15);
  check "sub 12 12" 12 (fun () -> Watchtower.sub 12 12);
  check "sub 25 1" 12 (fun () -> Watchtower.sub 25 1);

  print_endline "\n-- sub: zero is the identity --";
  check "sub 5 zero" 5 (fun () -> Watchtower.sub 5 Watchtower.zero);

  print_endline "\n-- add and sub cancel out --";
  check "sub (add 7 5) 5" 7 (fun () -> Watchtower.sub (Watchtower.add 7 5) 5);
  check "sub (add 9 8) 8" 9 (fun () -> Watchtower.sub (Watchtower.add 9 8) 8)
