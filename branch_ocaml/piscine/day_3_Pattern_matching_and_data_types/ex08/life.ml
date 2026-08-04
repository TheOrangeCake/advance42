(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   life.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 23:20:11 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/05 00:03:14 by hoannguy         ###   ########.fr       *)
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

(* ex07 *)
let rec generate_bases_triplets (r: rna) : (nucleobase * nucleobase * nucleobase) list =
  match r with
  | _ :: [] | _ :: _ :: [] | [] -> []
  | a :: b :: c :: rest -> (a, b, c) :: generate_bases_triplets rest

type aminoacid = 
  Stop | Ala | Arg | Asn | Asp | Cys | Gln | Glu | Gly | Tyr |
  His | Ile | Leu | Lys | Met | Phe | Pro | Ser | Thr | Trp | Val

type protein = aminoacid list

let string_of_aminoacid = function
| Stop -> ""
| Ala -> "Ala"
| Arg -> "Arg"
| Asn -> "Asn"
| Asp -> "Asp"
| Cys -> "Cys"
| Gln -> "Gln"
| Glu -> "Glu"
| Gly -> "Gly"
| Tyr -> "Tyr"
| His -> "His"
| Ile -> "Ile"
| Leu -> "Leu"
| Lys -> "Lys"
| Met -> "Met"
| Phe -> "Phe"
| Pro -> "Pro"
| Ser -> "Ser"
| Thr -> "Thr"
| Trp -> "Trp"
| Val -> "Val"

let rec string_of_protein (p: protein) : string =
  match p with
  | [] -> ""
  | h :: t -> string_of_aminoacid h ^ string_of_protein t

let decode_arn (r: rna) : protein =
  let bases_triplets = generate_bases_triplets r
  in let rec decode = function
    | [] -> []
    | ((U, A, A) | (U, A, G) | (U, G, A)) :: _ -> []
    | ((G, C, A) | (G, C, C) | (G, C, G) | (G, C, U)) :: rest -> Ala :: decode rest
    | ((A, G, A) | (A, G, G) | (C, G, A) | (C, G, C)
    | (C, G, G) | (C, G, U)) :: rest -> Arg :: decode rest
    | ((A, A, C) | (A, A, U)) :: rest -> Asn :: decode rest
    | ((G, A, C) | (G, A, U)) :: rest -> Asp :: decode rest
    | ((U, G, C) | (U, G, U)) :: rest -> Cys :: decode rest
    | ((C, A, A) | (C, A, G)) :: rest -> Gln :: decode rest
    | ((G, A, A) | (G, A, G)) :: rest -> Glu :: decode rest
    | ((G, G, A) | (G, G, C) | (G, G, G) | (G, G, U)) :: rest -> Gly :: decode rest
    | ((U, A, C) | (U, A, U)) :: rest -> Tyr :: decode rest
    | ((C, A, C) | (C, A, U)) :: rest -> His :: decode rest
    | ((A, U, A) | (A, U, C) | (A, U, U)) :: rest -> Ile :: decode rest
    | ((C, U, A) | (C, U, C) | (C, U, G) | (C, U, U)
    | (U, U, A) | (U, U, G)) :: rest -> Leu :: decode rest
    | ((A, A, A) | (A, A, G)) :: rest -> Lys :: decode rest
    | (A, U, G) :: rest -> Met :: decode rest
    | ((U, U, C) | (U, U, U)) :: rest -> Phe :: decode rest
    | ((C, C, A) | (C, C, C) | (C, C, G) | (C, C, U)) :: rest -> Pro :: decode rest
    | ((A, G, C) | (A, G, U) | (U, C, A) | (U, C, C)
    | (U, C, G) | (U, C, U)) :: rest -> Ser :: decode rest
    | ((A, C, A) | (A, C, C) | (A, C, G) | (A, C, U)) :: rest -> Thr :: decode rest
    | (U, G, G) :: rest -> Trp :: decode rest
    | ((G, U, A) | (G, U, C) | (G, U, G) | (G, U, U)) :: rest -> Val :: decode rest
    | _ :: _ -> []
  in decode bases_triplets

let print_triplets t =
  let rec print = function
    | [] -> print_newline ()
    | (a, b, c) :: rest ->
        print_string (string_of_nucleobase a);
        print_string (string_of_nucleobase b);
        print_string (string_of_nucleobase c);
        print_string " ";
        print rest
  in print t

(* ex08 *)
let life (s: string) =
  let helix = generate_helix 16 in
    let rna = generate_rna helix in
    let protein = decode_arn rna in
    print_string "Sample:            ";
    print_endline s;
    print_string "Generated helix:   ";
    print_endline (helix_to_string helix);
    print_string "Generated rna:     ";
    print_rna rna;
    print_string "Generated protein: ";
    print_endline (string_of_protein protein)

let () =
  life "Sample A";
  print_char '\n';
  life "Sample B";
  print_char '\n';
  life "Sample C";
  print_char '\n';
  life "Sample D"
