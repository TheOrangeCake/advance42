(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   rna.ml                                             :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 23:20:11 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/03 22:57:44 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

(* Ex04 *)
type phosphate = string
type deoxyribose = string
type nucleobase = | A | T | C | G | U | None
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

(* Ex05 *)
let rec rev acc lst =
  match lst with
  | [] -> acc
  | x :: rest -> rev (x :: acc) rest

type helix = nucleotide list

let generate_helix n : helix =
  Random.self_init ();
  let rec generate lst count =
    if count >= n
      then lst
    else
      let random = Random.int 4
      in let c = match random with
        | 0 -> 'A'
        | 1 -> 'T'
        | 2 -> 'C'
        | _ -> 'G'
      in generate ((generate_nucleotide c) :: lst) (count + 1)
    in rev [] (generate [] 0)

let string_of_nucleobase = function
    | A -> "A"
    | T -> "T"
    | C -> "C"
    | G -> "G"
    | U -> "U"
    | None -> ""

let helix_to_string (h: helix) : string =
  let rec convert = function
    | [] -> ""
    | (_, _, b) :: t -> (string_of_nucleobase b) ^ (convert t)
  in convert h

let print_helix (h: helix) : unit =
  print_endline (helix_to_string h)

let rec complementary_helix (h: helix) :helix =
  match h with
  | [] -> []
  | (p, d, b) :: rest -> 
      let base = match b with
      | A -> T
      | T -> A
      | C -> G
      | G -> C
      | U -> A
      | None -> None
      in (p, d, base) :: complementary_helix rest

(* ex06 *)
type rna = nucleobase list

let generate_rna (h: helix) : rna =
  let complement = complementary_helix h
  in let rec generate = function
    | [] -> []
    | (_, _, b) :: rest ->
        (if b = T then U else b) :: (generate rest)
  in generate complement

let print_rna (r: rna) : unit =
  let rec print = function
    | [] -> print_newline ()
    | b :: t -> print_string (string_of_nucleobase b); print t
  in print r

let () =
  let h = [generate_nucleotide 'A'; generate_nucleotide 'T';
            generate_nucleotide 'C'; generate_nucleotide 'G';
            generate_nucleotide 'A'] in
  assert (h = [("phosphate", "deoxyribose", A); ("phosphate", "deoxyribose", T);
                ("phosphate", "deoxyribose", C); ("phosphate", "deoxyribose", G);
                ("phosphate", "deoxyribose", A)]);
  assert (generate_rna h = [U; A; G; C; U]);
  assert (generate_rna [] = []);
  assert (generate_rna [generate_nucleotide 'x'] = [None]);
  let helix = generate_helix 6
  in let rna = generate_rna helix
  in
  print_helix helix;
  print_rna rna
