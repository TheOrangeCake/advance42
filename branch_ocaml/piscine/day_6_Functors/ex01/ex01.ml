(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ex01.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/28 00:24:40 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/30 13:30:14 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

module CustomString = struct
  type t = string

  let equal str1 str2 = str1 = str2

  (*  polynomial rolling hash function: cp-algorithms.com/string/string-hashing.html *)
  let hash str =
    let p = 53 in
    let m = 1_000_000_009 in
    let len = String.length str in
    let rec loop i p_pow acc =
      if i >= len then acc
      else
        let c = String.get str i in
        let acc = (acc + (int_of_char c * p_pow)) mod m in
        loop (i + 1) ((p_pow * p) mod m) acc
    in let res = loop 0 1 0 in
    (* Printf.printf "%s -> %d\n" str res; *)
    res
end

module StringHashtbl = Hashtbl.Make(CustomString)

let () =
  let ht = StringHashtbl.create 5 in
  let values = [ "Hello"; "world"; "42"; "Ocaml"; "H" ] in
  let pairs = List.map (fun s -> (s, String.length s)) values in
  List.iter (fun (k,v) -> StringHashtbl.add ht k v) pairs;
  StringHashtbl.iter (fun k v -> Printf.printf "k = \"%s\", v = %d\n" k v) ht
