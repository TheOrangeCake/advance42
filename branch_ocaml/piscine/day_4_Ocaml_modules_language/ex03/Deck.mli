module Card : sig
  module Color : sig
	type color = Spade | Heart | Diamond | Club
	val all : color list
	val toString : color -> string
	val toStringVerbose : color -> string
  end

  module Value : sig
	type value = T2 | T3 | T4 | T5 | T6 | T7 | T8 | T9 | T10 | Jack | Queen | King | As
	val all : value list
	val toInt : value -> int
	val toString : value -> string
	val toStringVerbose : value -> string
	val next : value -> value
	val previous : value -> value
  end

  type card
  val newCard : Value.value -> Color.color -> card
  val allSpades : card list
  val allHearts : card list
  val allDiamonds : card list
  val allClubs : card list
  val all : card list
  val getValue : card -> Value.value
  val getColor : card -> Color.color
  val toString : card -> string
  val toStringVerbose : card -> string
  val compare : card -> card -> int
  val max : card -> card -> card
  val min : card -> card -> card
  val best : card list -> card
  val isOf : card -> Color.color -> bool
  val isSpade : card -> bool
  val isHeart : card -> bool
  val isDiamond : card -> bool
  val isClub : card -> bool
end

type t
val newDeck : unit -> t
val toStringList : t -> string list
val toStringListVerbose : t -> string list
val drawCard : t -> Card.card * t
