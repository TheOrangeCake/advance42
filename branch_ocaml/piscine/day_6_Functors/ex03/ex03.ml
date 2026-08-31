(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ex03.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/30 14:01:39 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/30 23:20:37 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

module type FIXED = sig
  type t
  val of_float : float -> t
  val of_int : int -> t
  val to_float : t -> float
  val to_int : t -> int
  val to_string : t -> string
  val zero : t
  val one : t
  val succ : t -> t
  val pred : t -> t
  val min : t -> t -> t
  val max : t -> t -> t
  val gth : t -> t -> bool
  val lth : t -> t -> bool
  val gte : t -> t -> bool
  val lte : t -> t -> bool
  val eqp : t -> t -> bool (* physical equality *)
  val eqs : t -> t -> bool (* structural equality *)
  val add : t -> t -> t
  val sub : t -> t -> t
  val mul : t -> t -> t
  val div : t -> t -> t
  val foreach : t -> t -> (t -> unit) -> unit
end

(* ex03 *)
module type FRACTIONNAL_BITS = sig val bits: int end
module type MAKE = functor (F: FRACTIONNAL_BITS) -> FIXED

module Make: MAKE = functor (F: FRACTIONNAL_BITS) -> struct
  type t = int
  let scale = 1 lsl F.bits
  let scalef = float_of_int scale

  let of_float (x: float) = 
    if x >= 0. then x *. scalef |> (+.) 0.5 |> int_of_float
    else int_of_float (x *. scalef -. 0.5)

  let of_int (x: int) = x * scale
  let to_float (x: t) = float_of_int x /. scalef
  let to_int (x: t) = x / scale
  let to_string x = string_of_float (to_float x)
  let zero = 0
  let one = scale
  let succ (x: t) = x + 1
  let pred (x: t) = x - 1
  let min (x: t) (y: t) = if x < y then x else y
  let max (x: t) (y: t) = if x >= y then x else y
  let gth (x: t) (y: t) = x > y
  let lth (x: t) (y: t) = x < y
  let gte (x: t) (y: t) = x >= y
  let lte (x: t) (y: t) = x <= y
  let eqp (x: t) (y: t) = x == y
  let eqs (x: t) (y: t) = x = y
  let add (x: t) (y: t) = x + y
  let sub (x: t) (y: t) = x - y
  let mul (x: t) (y: t) = x * y / scale
  let div (x: t) (y: t) = (x * scale) / y
  let foreach (x: t) (y: t) (f: (t -> unit)) =
    let rec loop i =
      if i > y then ()
      else begin f i; loop (succ i) end
    in loop x
end

(* subject *)
module Fixed4 : FIXED = Make (struct let bits = 4 end)
module Fixed8 : FIXED = Make (struct let bits = 8 end)
let () =
  print_endline "----- SUBJECT TESTS";
  let x8 = Fixed8.of_float 21.10 in
  let y8 = Fixed8.of_float 21.32 in
  let r8 = Fixed8.add x8 y8 in
  print_endline (Fixed8.to_string r8);
  Fixed4.foreach (Fixed4.zero) (Fixed4.one) (fun f -> print_endline (Fixed4.to_string f));

  print_endline "\n----- ADDITIONAL TESTS";
  Printf.printf "Fixed 8 zero -> %f\n" (Fixed8.to_float Fixed8.zero);
  Printf.printf "Fixed 8 one -> %d\n" (Fixed8.to_int Fixed8.one);
  Printf.printf "Fixed 4 succ of zero -> %f\n" (Fixed4.zero |> Fixed4.succ |> Fixed4.to_float);
  Printf.printf "Fixed 4 pred of one -> %f\n" (Fixed4.one |> Fixed4.pred |> Fixed4.to_float);
  let x8 = Fixed8.of_float 32.42 in
  let x8' = Fixed8.of_float 32.42 in
  let y8 = Fixed8.of_int 10 in
  Printf.printf "Fixed 8 min of 32.42 and 10 -> %f\n" (Fixed8.min x8 y8 |> Fixed8.to_float);
  Printf.printf "Fixed 8 max of 32.42 and 10 -> %f\n" (Fixed8.max x8 y8 |> Fixed8.to_float);
  Printf.printf "Fixed 8 gth of 32.42 and 10 -> %s\n" (if Fixed8.gth x8 y8 then "true" else "false");
  Printf.printf "Fixed 8 lth of 32.42 and 10 -> %s\n" (if Fixed8.lth x8 y8 then "true" else "false");
  Printf.printf "Fixed 8 gte of 32.42 and 32.42 -> %s\n" (if Fixed8.gte x8 x8' then "true" else "false");
  Printf.printf "Fixed 8 lte of 32.42 and 10 -> %s\n" (if Fixed8.lte x8 y8 then "true" else "false");
  Printf.printf "Fixed 8 eqp of (x8=32.42) and (x8'=32.42) -> %s\n" (if Fixed8.eqp x8 x8' then "true" else "false");
  Printf.printf "Fixed 8 eqs of (x8=32.42) and (x8'=32.42) -> %s\n" (if Fixed8.eqs x8 x8' then "true" else "false");
  Printf.printf "Fixed 8 add of 32.42 and 10 -> %f\n" (Fixed8.add x8 y8 |> Fixed8.to_float);
  Printf.printf "Fixed 8 sub of 32.42 and 10 -> %f\n" (Fixed8.sub x8 y8 |> Fixed8.to_float);
  Printf.printf "Fixed 8 mul of 32.42 and 10 -> %f\n" (Fixed8.mul x8 y8 |> Fixed8.to_float);
  Printf.printf "Fixed 8 div of 32.42 and 10 -> %f\n" (Fixed8.div x8 y8 |> Fixed8.to_float);
  (* Printf.printf "Fixed 8 div of 32.42 and 0 -> %f\n" (Fixed8.div x8 (Fixed8.zero) |> Fixed8.to_float); *)
