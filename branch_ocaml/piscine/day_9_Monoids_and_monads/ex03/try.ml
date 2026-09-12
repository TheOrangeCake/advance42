(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   try.ml                                             :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/12 22:17:23 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/12 22:56:59 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

module Try = struct
  type 'a t = Success of 'a | Failure of exn

  let return (x: 'a) = Success x
  let bind (x: 'a t) (f: ('a -> 'b t)) =
    match x with
    | Success y-> begin
        try f y
        with _ -> Failure y
      end
    | Failure y -> Failure y

  let recover (x: 'a t) (f: (exn -> 'a t)) =
    match x with
    | Success y -> x
    | Failure y -> f y

  let filter (x: 'a t) (f: ('a -> bool)) =
    match x with
    | Success y -> if f y then x else Failure y
    | Failure y -> x

  let flatten (x: 'a t t) =
    match x with
    | Success Success y -> Success y
    | Success Failure y | Failure y -> Failure y
end
