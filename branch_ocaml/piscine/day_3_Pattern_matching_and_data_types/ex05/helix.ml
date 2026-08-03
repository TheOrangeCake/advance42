(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   helix.ml                                           :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/02 23:20:11 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/03 14:45:51 by hoannguy         ###   ########.fr       *)
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

let generate_helix n : helix=
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


        