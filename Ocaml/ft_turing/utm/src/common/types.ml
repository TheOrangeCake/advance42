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

type error =
	| Json_error of string
	| Missing_field of string
	| Invalid_symbol of string
	| Blank_not_in_alphabet
	| Unknown_state of string
	| Initial_not_in_states
	| Finals_not_subset_of_states
	| Duplicate_transitions of state * symbol
	| Machine_blocked of state * symbol
	| Default_error of string
