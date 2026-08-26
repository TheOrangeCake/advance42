(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   one_nn.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/25 22:28:22 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/26 16:01:02 by hoannguy         ###   ########.fr       *)
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

(* ex07 *)
let one_nn (lst: radar list) (r: radar) : string =
  let best = ref (eu_dist (List.nth lst 0 |> fst) (r |> fst)) in
  let ret = ref (List.nth lst 0 |> snd) in
  List.iter (fun (x: radar) ->
    let dist = eu_dist (fst x) (fst r) in
    if dist < !best
      then begin
        best := dist;
        ret := snd x
      end) lst;
  !ret

(* tests *)
let check (name: string) (expected: string) (got: string) : unit =
  let status = if expected = got then "[ OK ] " else "[FAIL] " in
  print_endline (status ^ name ^ ": expected \"" ^ expected ^ "\", got \"" ^ got ^ "\"")

let title (s: string) : unit =
  print_newline ();
  print_endline ("--- " ^ s ^ " ---")

(* is this vector part of the training set? *)
let is_known (train: radar list) (v: float array) : bool =
  List.exists (fun (tv, _) -> tv = v) train

(* runs one_nn on every radar of [set] and reports how many labels it got right *)
let accuracy (name: string) (train: radar list) (set: radar list) : unit =
  let total = ref 0 in
  let correct = ref 0 in
  List.iter (fun (v, label) ->
    incr total;
    if one_nn train (v, label) = label
      then incr correct) set;
  if !total = 0
    then print_endline (name ^ ": no radar to test")
  else
    print_endline (name ^ ": " ^ string_of_int !correct ^ "/" ^ string_of_int !total
      ^ " correct (" ^ string_of_int (!correct * 100 / !total) ^ "%)")

let () =
  title "in-memory, 2 features";
  let test1 = [
    ([|0.; 0.|],     "A");
    ([|10.; 10.|],   "B");
    ([|0.1; 0.1|],   "C");
    ([|99.; 99.|],   "Z");
  ] in
  check "exactly on A"   "A" (one_nn test1 ([|0.; 0.|], "A"));
  check "closest to C"   "C" (one_nn test1 ([|0.2; 0.2|], "C"));
  check "closest to B"   "B" (one_nn test1 ([|9.; 9.|], "B"));
  check "closest to Z"   "Z" (one_nn test1 ([|60.; 60.|], "Z"));

  title "a radar it gets wrong";
  let tricky = [
    ([|0.; 0.|], "A");
    ([|1.; 0.|], "B");
  ] in
  let real = "A" in
  let guess = one_nn tricky ([|0.9; 0.|], real) in
  print_endline ("real class \"" ^ real ^ "\", guessed \"" ^ guess ^ "\""
    ^ (if guess = real then " -> right" else " -> wrong, as expected"));

  title "custom.csv: 3 features, classes cat/dog/bird";
  let animals = examples_of_file "./custom.csv" in
  if animals = []
    then print_endline "could not read ./custom.csv"
  else begin
    check "near the cats"  "cat"  (one_nn animals ([|0.05; 0.05; 0.05|], "cat"));
    check "near the dogs"  "dog"  (one_nn animals ([|4.9; 5.1; 5.1|], "dog"));
    check "near the birds" "bird" (one_nn animals ([|-3.0; 2.0; 7.1|], "bird"))
  end;

  title "ionosphere";
  let train = examples_of_file "./ionosphere.train.csv" in
  let test = examples_of_file "./ionosphere.test.csv" in
  if train = [] || test = []
    then print_endline "could not read the ionosphere csv files"
  else begin
    let unseen = List.filter (fun (v, _) -> not (is_known train v)) test in
    accuracy "whole test set    " train test;
    accuracy "unseen radars only" train unseen;
  end
