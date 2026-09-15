(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   ex04.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/30 23:21:49 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/31 18:45:01 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

(* subject *)
module type VAL =
sig
  type t
  val add : t -> t -> t
  val mul : t -> t -> t
end

(* ex04 *)
module type EVALEXPR =
sig
  type t
  type expr = 
    | Add of expr * expr
    | Mul of expr * expr
    | Value of t
  val eval: expr -> t
end

module type MAKEEVALEXPR =  functor (F: VAL) -> EVALEXPR with type t := F.t
module MakeEvalExpr : MAKEEVALEXPR = functor (F: VAL) ->
struct
  type t = F.t
  type expr =
    | Add of expr * expr
    | Mul of expr * expr
    | Value of t
  let rec eval (ex: expr) =
    match ex with
    | Add (a, b) -> F.add (eval a) (eval b)
    | Mul (a, b) -> F.mul (eval a) (eval b)
    | Value n -> n
end

(* subject *)
module IntVal : VAL with type t = int =
struct
  type t = int
  let add = ( + )
  let mul = ( * )
end

module FloatVal : VAL with type t = float =
struct
  type t = float
  let add = ( +. )
  let mul = ( *. )
end

module StringVal : VAL with type t = string =
struct
  type t = string
  let add s1 s2 = if (String.length s1) > (String.length s2) then s1 else s2
  let mul = ( ^ )
end

module IntEvalExpr : EVALEXPR with type t := int = MakeEvalExpr (IntVal)
module FloatEvalExpr : EVALEXPR with type t := float = MakeEvalExpr (FloatVal)
module StringEvalExpr : EVALEXPR with type t := string = MakeEvalExpr (StringVal)

let ie = IntEvalExpr.Add (IntEvalExpr.Value 40, IntEvalExpr.Value 2)
let fe = FloatEvalExpr.Add (FloatEvalExpr.Value 41.5, FloatEvalExpr.Value 0.92)
let se = StringEvalExpr.Mul (StringEvalExpr.Value "very ",
                            (StringEvalExpr.Add (StringEvalExpr.Value "very long",
                                                  StringEvalExpr.Value "short")))

let () = Printf.printf "Res = %d\n" (IntEvalExpr.eval ie)
let () = Printf.printf "Res = %f\n" (FloatEvalExpr.eval fe)
let () = Printf.printf "Res = %s\n" (StringEvalExpr.eval se)