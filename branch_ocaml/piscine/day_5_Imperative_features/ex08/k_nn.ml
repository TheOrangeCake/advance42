(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   k_nn.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/25 22:28:22 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/26 17:05:53 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

type radar = float array * string

(* embeded ex06 *)
let examples_of_file (path: string) : radar list =
  try
    In_channel.with_open_text path (fun ic ->
        let acc = ref [] in
        (
          try
            while true do
              acc := input_line ic :: !acc
            done
          with End_of_file -> ()
        );
        
        let list_len = List.length !acc in
        if list_len = 0
          then failwith "Empty file"
        else
          let lst = ref [] in
          List.iter (fun line ->
            let fields = Array.of_list (String.split_on_char ',' line) in
            if Array.length fields < 2
              then failwith "Need at least 1 float column and 1 string column at the end"
            else
              let nb_of_float = (Array.length fields) - 1 in
              let f_array = Array.create_float nb_of_float in
              for idx = 0 to nb_of_float - 1 do
                f_array.(idx) <- (fields.(idx) |> float_of_string)
              done;
              lst := (f_array, fields.(nb_of_float)) :: !lst
          ) !acc;
          !lst
      )
  with
  | Sys_error e | Failure e -> print_endline ("Bad csv: " ^ e); []

(* embeded ex05 *)
let f pa pb = (pa -. pb) ** 2. 

let eu_dist (a: float array) (b: float array) =
  let len_a = Array.length a in
  let len_b = Array.length b in
  if len_a <= 0 || len_b <= 0
    then 0.0
  else
    let acc = ref 0. in

    let len =
      if len_a < len_b
        then len_a
      else len_b
    in
    
    for i = 0 to len - 1 do
      acc := !acc +. f a.(i) b.(i)
    done;
    sqrt !acc

(* ex08 *)
let k_nn (lst: radar list) (k: int) (r: radar) =
  if k <= 0
    then failwith "Invalid K"
  else
    let len = List.length lst in
    let k' =
      if k > len then len
      else k
    (* calculate then store eu_dist for each radar in a list, 
      then sort that list *)
    in
    let dst_lst : (float * string) list ref = ref [] in
    List.iter (fun (x: radar) ->
      let dst = eu_dist (fst x) (fst r) in
      dst_lst := (dst, x |> snd) :: !dst_lst
    ) lst;
    let sorted = List.sort (fun x y ->
      if fst x > fst y then 1
      else if fst x = fst y then 0
      else -1) !dst_lst
    (* Count the number of occurence for each label *)
    in
    let arr = Array.of_list sorted in
    let counts : (string * int ref) list ref = ref [] in
    for i = 0 to k' - 1 do
      let label = snd arr.(i) in
      (match List.assoc_opt label !counts with
       | Some c -> incr c
       | None -> counts := (label, ref 1) :: !counts)
    done;

    let best = ref 0 in
    List.iter (fun (_, c) -> if !c > !best then best := !c) !counts;

    (* Return the nearest with highest occurence *)
    let ret = ref (snd arr.(0)) in
    for i = k' - 1 downto 0 do
      let label = snd arr.(i) in
      if !(List.assoc label !counts) = !best
        then ret := label
    done;
    !ret
