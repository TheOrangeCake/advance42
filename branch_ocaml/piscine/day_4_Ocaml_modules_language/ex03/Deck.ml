(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   Deck.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/12 15:02:48 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/16 20:36:55 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

(* Card *)
module Card = struct

	(* Color *)
	module Color = struct
		type color = Spade | Heart | Diamond | Club

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
		type value = T2 | T3 | T4 | T5 | T6 | T7 | T8 | T9 | T10 | Jack | Queen | King | As

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

	type card = Value.value * Color.color

	let newCard (v : Value.value) (c: Color.color) : card = (v, c)

	let allSpades : card list = List.map (fun v -> newCard v Color.Spade) Value.all
	let allHearts : card list = List.map (fun v -> newCard v Color.Heart) Value.all
	let allDiamonds : card list = List.map (fun v -> newCard v Color.Diamond) Value.all
	let allClubs : card list = List.map (fun v -> newCard v Color.Club) Value.all
	let all : card list = allSpades @ allHearts @ allDiamonds @ allClubs

	let getValue : card -> Value.value = function (v , _) -> v
	let getColor : card -> Color.color = function (_, c) -> c

	let toString card = Value.toString (getValue card) ^ Color.toString (getColor card)

	let toStringVerbose card =
		"Card("
		^ Value.toStringVerbose (getValue card)
		^ ", "
		^ Color.toStringVerbose (getColor card)
		^ ")"

	let compare card1 card2 =
		let v1i = Value.toInt (getValue card1) in
		let v2i = Value.toInt (getValue card2) in
		if v1i = v2i then 0
		else if v1i < v2i then -1
		else 1

	let max card1 card2 =
		if Value.toInt (getValue card1) >= Value.toInt (getValue card2) then card1
		else card2

	let min card1 card2 =
		if Value.toInt (getValue card1) <= Value.toInt (getValue card2) then card1
		else card2

	let best lst =
		match lst with
		| [] -> invalid_arg "Empty list"
		| card :: rest -> List.fold_left max card rest

	let isOf card color = getColor card = color
	let isSpade card = getColor card = Color.Spade
	let isHeart card = getColor card = Color.Heart
	let isDiamond card = getColor card = Color.Diamond
	let isClub card = getColor card = Color.Club
end

type t = Card.card list

let newDeck () =
	Random.init;;
	