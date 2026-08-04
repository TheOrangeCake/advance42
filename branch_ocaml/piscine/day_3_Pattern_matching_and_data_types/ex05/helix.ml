(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   helix.ml                                           :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 23:20:11 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/03 16:13:28 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

(* Ex04 *)
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
    | None -> ""

let helix_to_string (h: helix) : string =
  let rec convert = function
    | [] -> ""
    | (_, _, b) :: t -> (string_of_nucleobase b) ^ (convert t)
  in convert h

let rec complementary_helix (h: helix) :helix =
  match h with
  | [] -> []
  | (p, d, b) :: rest -> 
      let base = match b with
      | A -> T
      | T -> A
      | C -> G
      | G -> C
      | None -> None
      in (p, d, base) :: complementary_helix rest

let () =
  let h = [generate_nucleotide 'A'; generate_nucleotide 'T';
            generate_nucleotide 'C'; generate_nucleotide 'G'] in
  assert (helix_to_string h = "ATCG");
  assert (helix_to_string (complementary_helix h) = "TAGC");
  assert (generate_helix 0 = []);
  assert (helix_to_string [] = "");
  assert (String.length (helix_to_string (generate_helix 30)) = 30);
  let helix = generate_helix 6
  in let complement = complementary_helix helix
  in 
  print_endline (helix_to_string helix);
  print_endline (helix_to_string complement)
