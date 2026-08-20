(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ft_ref.ml                                          :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/19 23:36:06 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/20 19:11:16 by hoannguy         ###   ########.fr       *)
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

let increase v = Ref.return |> Ref.set v
