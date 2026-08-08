(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   Card.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/07 23:20:15 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/08 14:06:03 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

(* Color *)
module Color = struct
  type t = Spade | Heart | Diamond | Club

  let all = [Spade; Heart; Diamond; Club]

  let toString = function
    | Spade -> "S"
    | Heart -> "H"
    | Diamond -> "D"
    | Club -> "C"

  let toStringVerbose = function
    | Spade -> "Spade"
    | Heart -> "Heart"
    | Diamond -> "Diamond"
    | Club -> "Club"
end

(* Value *)
module Value = struct
  type t = T2 | T3 | T4 | T5 | T6 | T7 | T8 | T9 | T10 | Jack | Queen | King | As

  let all = [T2 ; T3 ; T4 ; T5 ; T6 ; T7 ; T8 ; T9 ; T10 ; Jack ; Queen ; King ; As]

  let toInt = function
  | T2 -> 1
  | T3 -> 2
  | T4 -> 3
  | T5 -> 4
  | T6 -> 5
  | T7 -> 6
  | T8 -> 7
  | T9 -> 8
  | T10 -> 9
  | Jack -> 10
  | Queen -> 11
  | King -> 12
  | As -> 13

  let toString = function
  | T2 -> "2"
  | T3 -> "3"
  | T4 -> "4"
  | T5 -> "5"
  | T6 -> "6"
  | T7 -> "7"
  | T8 -> "8"
  | T9 -> "9"
  | T10 -> "10"
  | Jack -> "J"
  | Queen -> "Q"
  | King -> "K"
  | As -> "A"

  let toStringVerbose = function
  | T2 -> "2"
  | T3 -> "3"
  | T4 -> "4"
  | T5 -> "5"
  | T6 -> "6"
  | T7 -> "7"
  | T8 -> "8"
  | T9 -> "9"
  | T10 -> "10"
  | Jack -> "Jack"
  | Queen -> "Queen"
  | King -> "King"
  | As -> "As"

  let next = function
  | T2 -> T3
  | T3 -> T4
  | T4 -> T5
  | T5 -> T6
  | T6 -> T7
  | T7 -> T8
  | T8 -> T9
  | T9 -> T10
  | T10 -> Jack
  | Jack -> Queen
  | Queen -> King
  | King -> As
  | As -> invalid_arg "As is the highest value"

  let previous = function
  | T2 -> invalid_arg "T2 is the lowest value"
  | T3 -> T2
  | T4 -> T3
  | T5 -> T4
  | T6 -> T5
  | T7 -> T6
  | T8 -> T7
  | T9 -> T8
  | T10 -> T9
  | Jack -> T10
  | Queen -> Jack
  | King -> Queen
  | As -> King
end

(* Card *)
type t = Value.t * Color.t

let newCard (v : Value.t) (c: Color.t) : t =
  (v, c)

let allSpades : t list =
  [(Value.T2, Color.Spade); (Value.T3, Color.Spade); (Value.T4, Color.Spade);
   (Value.T5, Color.Spade); (Value.T6, Color.Spade); (Value.T7, Color.Spade);
   (Value.T8, Color.Spade); (Value.T9, Color.Spade); (Value.T10, Color.Spade);
   (Value.Jack, Color.Spade); (Value.Queen, Color.Spade); (Value.King, Color.Spade);
   (Value.As, Color.Spade)]

let allHearts : t list =
  [(Value.T2, Color.Heart); (Value.T3, Color.Heart); (Value.T4, Color.Heart);
   (Value.T5, Color.Heart); (Value.T6, Color.Heart); (Value.T7, Color.Heart);
   (Value.T8, Color.Heart); (Value.T9, Color.Heart); (Value.T10, Color.Heart);
   (Value.Jack, Color.Heart); (Value.Queen, Color.Heart); (Value.King, Color.Heart);
   (Value.As, Color.Heart)]
