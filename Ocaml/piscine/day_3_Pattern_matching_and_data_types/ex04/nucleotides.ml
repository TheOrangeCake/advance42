(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   nucleotides.ml                                     :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 22:31:45 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/02 23:17:13 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

type phosphate = string
type deoxyribose = string
type nucleobase = | A | T | C | G | None

type nucleotide = phosphate * deoxyribose * nucleobase

let generate_nucleotide (c: char) : nucleotide =
  let base =
    match c with
    | 'A' | 'a' -> A
    | 'T' | 't' -> T
    | 'C' | 'c' -> C
    | 'G' | 'g' -> G
    | _ -> None

  in ("phosphate", "deoxyribose", base)

let print_nucleotide ((p, d, b): nucleotide) =
  let base = match b with
    | A -> "A"
    | T -> "T"
    | C -> "C"
    | G -> "G"
    | None -> "None"

  in print_string "("; print_string p; print_string ", ";
     print_string d; print_string ", "; print_string base;
     print_endline ")"

let print_test c =
  print_string "generate_nucleotide '";
  print_char c;
  print_string "' = ";
  print_nucleotide (generate_nucleotide c)

let () =
  assert (generate_nucleotide 'A' = ("phosphate", "deoxyribose", A));
  assert (generate_nucleotide 'T' = ("phosphate", "deoxyribose", T));
  assert (generate_nucleotide 'C' = ("phosphate", "deoxyribose", C));
  assert (generate_nucleotide 'G' = ("phosphate", "deoxyribose", G));
  assert (generate_nucleotide 'a' = ("phosphate", "deoxyribose", A));
  assert (generate_nucleotide 'x' = ("phosphate", "deoxyribose", None));
  assert (generate_nucleotide ' ' = ("phosphate", "deoxyribose", None));

  print_test 'A';
  print_test 'T';
  print_test 'C';
  print_test 'G';
  print_test 'a';
  print_test 'x';
  print_test ' '
