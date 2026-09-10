(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   calc.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/10 22:15:05 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/10 23:04:56 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)


module type MONOID = sig
  type element
  val zero1 : element
  val zero2 : element
  val mul : element -> element -> element
  val add : element -> element -> element
  val div : element -> element -> element
  val sub : element -> element -> element
end

module INT = struct
  type element = int

  let zero1 = 0
  let zero2 = 1
  let add a b = a + b
  let sub a b = a - b
  let mul a b = a * b
  let div a b = a / b
end

module FLOAT = struct
  type element = float

  let zero1 = 0.
  let zero2 = 1.
  let add a b = a +. b
  let sub a b = a -. b
  let mul a b = a *. b
  let div a b = a /. b
end

module Calc = functor (M: MONOID) -> struct
  let add (a: M.element) (b: M.element) = M.add a b
  let sub (a: M.element) (b: M.element) = M.sub a b
  let mul (a: M.element) (b: M.element) = M.mul a b
  let div (a: M.element) (b: M.element) = M.div a b

  let power (x: M.element) (b: int) =
    if b <= 0 then invalid_arg "Error: Second parameter is not positive"
    else
      let rec loop i =
        if i <= 0 then M.zero2
        else M.mul x (loop (i - 1))
      in loop b

  let fact (n: M.element) =
    if n < M.zero1 then invalid_arg "Error: Argument is not positive"
    else
      let rec loop i =
        if i <= M.zero2 then M.zero2
        else M.mul i (loop (M.sub i M.zero2))
      in loop n
end

module Calc_int = Calc (INT)
module Calc_float = Calc (FLOAT)

let show name value = print_endline (name ^ " = " ^ value)
let show_int name value = show name (string_of_int value)
let show_float name value = show name (string_of_float value)

let show_error name f =
  try show name (string_of_int (f ()))
  with Invalid_argument msg -> show name msg

let () =
  print_endline "--- INT ---";
  show_int "add 20 1" (Calc_int.add 20 1);
  show_int "sub 20 1" (Calc_int.sub 20 1);
  show_int "mul 20 3" (Calc_int.mul 20 3);
  show_int "div 20 3" (Calc_int.div 20 3);
  show_int "mul (add 20 1) 2" (Calc_int.mul (Calc_int.add 20 1) 2);
  show_int "power 3 3" (Calc_int.power 3 3);
  show_int "power 2 10" (Calc_int.power 2 10);
  show_int "power 5 1" (Calc_int.power 5 1);
  show_int "fact 0" (Calc_int.fact 0); (* www.geeksforgeeks.org/maths/zero-factorial/ *)
  show_int "fact 1" (Calc_int.fact 1);
  show_int "fact 5" (Calc_int.fact 5);
  print_endline "--- FLOAT ---";
  show_float "add 20. 1." (Calc_float.add 20.0 1.0);
  show_float "sub 20. 1." (Calc_float.sub 20.0 1.0);
  show_float "mul 20. 3." (Calc_float.mul 20.0 3.0);
  show_float "div 20. 3." (Calc_float.div 20.0 3.0);
  show_float "mul (add 20. 1.) 2." (Calc_float.mul (Calc_float.add 20.0 1.0) 2.0);
  show_float "power 3. 3" (Calc_float.power 3.0 3);
  show_float "power 0.5 3" (Calc_float.power 0.5 3);
  show_float "fact 0." (Calc_float.fact 0.0);
  show_float "fact 5." (Calc_float.fact 5.0);
  print_endline "--- errors ---";
  show_error "power 3 0" (fun () -> Calc_int.power 3 0);
  show_error "power 3 (-2)" (fun () -> Calc_int.power 3 (-2));
  show_error "fact (-5)" (fun () -> Calc_int.fact (-5))
