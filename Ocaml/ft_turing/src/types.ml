type action = Left | Right

type symbol = char

type symbol_set = Core.Char.Set.t

type state = string

type state_set = Core.String.Set.t

type transition = {
	read		: symbol;
	to_state: state;
	write		: symbol;
	action	: action
}

type transition_table = (state * symbol, transition) Core.Map.Poly.t

type turing = {
	name				: string;
	alphabet		: symbol_set;
	blank				: symbol;
	states			: state_set;
	initial			: state;
	finals			: state_set;
	transitions	: transition_table
}

type tape = {
	left	: symbol list;
	head	: symbol;
	right	: symbol list;
}

type error =
	| Json_error of string
	| Missing_field of string
	| Invalid_symbol of string
	| Blank_not_in_alphabet
	| Unknown_state of string
	| Initial_not_in_states
	| Finals_not_subset_of_states
	| Duplicate_transitions of state * symbol
	| Invalid_transition of string
	| Machine_blocked of state * symbol
	| Default_error of string

(* Normal -> Json path + input
	 Complexity -> Json path + inputfile *)
type mode = Normal of (string * string) | Complexity of (string * string)

type tape_len = int

type steps = int

type message = string

type time = (tape_len * steps)

type time_data = Accept of time | Reject of (tape_len * message)

type bigO =
	Constant | Logarithmic | Linear | Linearithmic |
	Quadratic | Cubic | Exponential | Factorial


type complexity = {
	bigO	: bigO;
	m			: float;
	b			: float;
	error	: float;
}
