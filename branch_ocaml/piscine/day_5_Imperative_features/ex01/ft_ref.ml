(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_ref.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/19 23:36:06 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/21 00:29:20 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

type 'a ft_ref = { mutable value: 'a}

module type REF = sig
  val return : 'a -> 'a ft_ref
  val get : 'a ft_ref -> 'a
  val set : 'a ft_ref -> 'a -> unit
  val bind : 'a ft_ref -> ('a -> 'b ft_ref) -> 'b ft_ref
end

(* Resource: http://dev.realworldocaml.org/imperative-programming.html *)
module Ref : REF = struct
  let return (v : 'a) : 'a ft_ref = { value = v }
  let get (v_ref : 'a ft_ref) = v_ref.value
  let set (v_ref : 'a ft_ref) (v : 'a) = v_ref.value <- v
  let bind (v_ref : 'a ft_ref) (f : ('a -> 'b ft_ref)) = get v_ref |> f
end

let intToFloat v = Ref.return (float_of_int v)

let () =
  let ref1 = Ref.return 42 in
  print_string "Creating new ref1 of value -> ";
  print_int (Ref.get ref1);
  print_char '\n';

  let ref2 = Ref.return 42 in
  print_string "Creating new ref2 of value -> ";
  print_int (Ref.get ref2);
  print_char '\n';

  print_string "Is ref1 equal to ref2 structurely? -> ";
  if (ref1 = ref2) then print_endline "True" else print_endline "False";
  
  print_string "Is ref1 equal to ref2 physicaly? -> ";
  if (ref1 == ref2) then print_endline "True" else print_endline "False";

  print_string "Mutate ref1 value to 1337 -> ";
  Ref.set ref1 1337;
  print_int (Ref.get ref1);
  print_char '\n';

  print_string "Is ref1 still equal to ref2 structurely? -> ";
  if (ref1 = ref2) then print_endline "True" else print_endline "False";

  print_string "Bind ref1 with intToFloat function -> ";
  print_float (Ref.bind ref1 intToFloat |> Ref.get);
  print_char '\n';

  print_string "What is ref1 value now -> ";
  print_int (Ref.get ref1);
  print_char '\n';
