(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   try.ml                                             :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/12 22:17:23 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/13 17:04:07 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

type 'a t = Success of 'a | Failure of exn

let return (x: 'a) = Success x

let bind (x: 'a t) (f: ('a -> 'b t)) =
  match x with
  | Success y-> begin
      try f y
      with e -> Failure e
    end
  | Failure y -> Failure y

let recover (x: 'a t) (f: (exn -> 'a t)) =
  match x with
  | Success _ -> x
  | Failure y -> f y

let filter (x: 'a t) (f: ('a -> bool)) =
  match x with
  | Success y -> begin
      try 
        if f y then x
        else Failure (Invalid_argument "Predicate was not satisfied")
      with e -> Failure e
    end
  | Failure _ -> x

let flatten (x: 'a t t) =
  match x with
  | Success (Success y) -> Success y
  | Success (Failure y) | Failure y -> Failure y
