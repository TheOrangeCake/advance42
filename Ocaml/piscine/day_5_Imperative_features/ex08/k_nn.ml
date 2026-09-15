(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   k_nn.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/25 22:28:22 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/26 17:14:50 by hoannguy         ###   ########.fr       *)
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


(* Tests *)
let check (name: string) (expected: string) (got: string) : unit =
  let status = if expected = got then "[ OK ] " else "[FAIL] " in
  print_endline (status ^ name ^ ": expected \"" ^ expected ^ "\", got \"" ^ got ^ "\"")

let title (s: string) : unit =
  print_newline ();
  print_endline ("--- " ^ s ^ " ---")

let is_known (train: radar list) (v: float array) : bool =
  List.exists (fun (tv, _) -> tv = v) train

(* Runs k_nn on every radar of [set] and reports how many labels it got right *)
let accuracy (train: radar list) (set: radar list) (k: int) : unit =
  let total = ref 0 in
  let correct = ref 0 in
  List.iter (fun (v, label) ->
    incr total;
    if k_nn train k (v, label) = label
      then incr correct) set;
  if !total = 0
    then print_endline ("  k=" ^ string_of_int k ^ ": no radar to test")
  else
    print_endline ("  k=" ^ string_of_int k ^ ": "
      ^ string_of_int !correct ^ "/" ^ string_of_int !total
      ^ " correct (" ^ string_of_int (!correct * 100 / !total) ^ "%)")

let () =
  title "k changes the answer";
  (* two g on the left, three b on the right *)
  let mixed = [
    ([|0.|], "g");
    ([|1.|], "g");
    ([|2.|], "b");
    ([|3.|], "b");
    ([|4.|], "b");
  ] in
  let q = ([|1.4|], "") in
  (* neighbours by distance: g(0.4) b(0.6) g(1.4) b(1.6) b(2.6) *)
  check "k=1, nearest is g      " "g" (k_nn mixed 1 q);
  check "k=3, 2 g beat 1 b      " "g" (k_nn mixed 3 q);
  check "k=5, 3 b beat 2 g      " "b" (k_nn mixed 5 q);

  title "even k, tie broken by the nearest radar";
  (* k=2 picks one g and one b, so the closer one decides *)
  check "tie 1-1, nearest is g  " "g" (k_nn mixed 2 ([|1.4|], ""));
  check "tie 1-1, nearest is b  " "b" (k_nn mixed 2 ([|1.8|], ""));
  check "tie 2-2, nearest is b  " "b" (k_nn mixed 4 ([|1.8|], ""));

  title "k larger than the training set is clamped";
  check "k=99 behaves like k=5  " (k_nn mixed 5 q) (k_nn mixed 99 q);

  title "invalid k";
  (try
    let _ = k_nn mixed 0 q in
    print_endline "[FAIL] k=0: no exception raised"
  with Failure e -> print_endline ("[ OK ] k=0 rejected: " ^ e));

  title "custom.csv: 3 features, classes cat/dog/bird";
  let animals = examples_of_file "./custom.csv" in
  if animals = []
    then print_endline "could not read ./custom.csv"
  else begin
    check "near the cats          " "cat"  (k_nn animals 3 ([|0.05; 0.05; 0.05|], ""));
    check "near the dogs          " "dog"  (k_nn animals 3 ([|4.9; 5.1; 5.1|], ""));
    check "near the birds         " "bird" (k_nn animals 3 ([|-3.0; 2.0; 7.1|], ""));
    (* 9 examples, 3 per class: k=9 is a 3-way tie, the nearest one wins *)
    check "3-way tie, nearest cat " "cat"  (k_nn animals 9 ([|0.05; 0.05; 0.05|], ""));
    check "3-way tie, nearest dog " "dog"  (k_nn animals 9 ([|4.9; 5.1; 5.1|], ""))
  end;

  title "ionosphere: accuracy for a few k";
  let train = examples_of_file "./ionosphere.train.csv" in
  let test = examples_of_file "./ionosphere.test.csv" in
  if train = [] || test = []
    then print_endline "could not read the ionosphere csv files"
  else begin
    let unseen = List.filter (fun (v, _) -> not (is_known train v)) test in
    print_endline "whole test set:";
    List.iter (accuracy train test) [1; 3; 5; 7; 9];
    print_endline "unseen radars only:";
    List.iter (accuracy train unseen) [1; 3; 5; 7; 9]
  end
