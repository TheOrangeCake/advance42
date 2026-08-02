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

let () =
  assert (generate_nucleotide 'A' = ("phosphate", "deoxyribose", A));
  assert (generate_nucleotide 'T' = ("phosphate", "deoxyribose", T));
  assert (generate_nucleotide 'C' = ("phosphate", "deoxyribose", C));
  assert (generate_nucleotide 'G' = ("phosphate", "deoxyribose", G));
  assert (generate_nucleotide 'a' = ("phosphate", "deoxyribose", A));
  assert (generate_nucleotide 'x' = ("phosphate", "deoxyribose", None));
  assert (generate_nucleotide ' ' = ("phosphate", "deoxyribose", None))
