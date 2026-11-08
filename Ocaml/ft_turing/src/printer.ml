open Types

let reset = "\027[0m"
let bold = "\027[1m"
let gray = "\027[90m"
let red = "\027[31m"
let green = "\027[32m"
let yellow = "\027[33m"
let blue = "\027[34m"
let magenta = "\027[35m"
let cyan = "\027[36m"

let print_help () =
	print_endline (
		"usage: ft_turing [-h] jsonfile input\n"										^
		"       ft_turing [-h] jsonfile -c inputs_file\n\n"								^
		"positional arguments:\n"														^
		"    jsonfile                        json description of the machine\n"		^
		"    input                           input of the machine\n\n"					^
		"optional arguments:\n"															^
		"    -h, --help                      show this help message and exit\n"		^
		"    -c, --complexity inputs_file    estimate the time complexity of the\n"	^
		"                                    machine from the inputs in inputs_file\n"	^
		"                                    (one input per line, at least 3 of different lengths)"
	)

let print_banner () =
	print_endline "";
	print_endline (cyan ^ {|
 ███████████████████████████████████████████████████████████████████████████████

                                   o    o    o
                                   ║    ║    ║
                                 ┌─╨────╨────╨─┐
      .-""""""-.                 │ ╔═╗ ▓▓▓ ╔═╗ │                 .-""""""-.
    .'          '.               │ ║▒║ ▓▓▓ ║▒║ │               .'          '.
   /              \              │ ╚═╝ ▓▓▓ ╚═╝ │              /              \
  |       ()       |             └──────┬──────┘             |       ()       |
   \              /_____________________▼_____________________\              /
    '.          .' |0|1|0|1|1|0|1|0|0|1|1|1|0|0|1|0|1|1|0|1|0| '.          .'
   ┌──'-.____.-'──┐‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾┌──'-.____.-'──┐
   │              │       ┌───────────────────────────┐       │              │
   │              │       │ ┌───────────────────────┐ │       │              │
   │              │       │ │       ft_turing       │ │       │              │
   └──────────────┘       │ └───────────────────────┘ │       └──────────────┘
 ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄
 ███████████████████████████████████████████████████████████████████████████████
  ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
|} ^ reset)

let print_start () =
	print_endline (bold ^ yellow ^ "Machine started !" ^ reset);
	print_endline "│"

let print_error (error: string) =
  prerr_endline (bold ^ red ^ "Error: " ^ reset ^ error)

let print_success (success: string) =
	print_endline (bold ^ green ^ "Success: " ^ reset ^ success)

let print_message (message: string) =
	print_endline message

let print_tape t =
	let rec print_char_list lst =
		match lst with
		| [] -> ()
		| head::tail -> print_char head; print_char_list tail
	in
	print_string (yellow ^ "[ " ^ reset);
	print_char_list (List.rev t.left);
	(* print_string (cyan ^ "<" ^ reset); *)
	print_string (bold ^ magenta ^ String.make 1 t.head ^ reset);
	(* print_string (cyan ^ ">" ^ reset); *)
	print_char_list t.right;
	print_string (yellow ^ " ]" ^ reset)

let print_transition (state: state) (t: transition) =
	let action = match t.action with
	| Right -> "RIGHT"
	| Left -> "LEFT"
	in
	print_string (
		"(" ^ state ^ ", " ^ 
		String.make 1 t.read ^ ")");
	print_string (gray ^ " -> " ^ reset);
	print_string ( "(" ^
		t.to_state ^ ", " ^ String.make 1 t.write ^
		", " ^ action ^ ")"
	)

let print_machine (machine: turing) =
	let label name = blue ^ name ^ reset in
	let to_list lst = "[ " ^ String.concat ", " lst ^ " ]" in
	let alphabet = List.map (String.make 1) (Core.Set.to_list machine.alphabet) in
	print_endline "│";
	print_endline (bold ^ yellow ^ machine.name ^ reset);
	print_endline (label "Alphabet: " ^ to_list alphabet);
	print_endline (label "Blank:    " ^ String.make 1 machine.blank);
	print_endline (label "States:   " ^ to_list (Core.Set.to_list machine.states));
	print_endline (label "Initial:  " ^ machine.initial);
	print_endline (label "Finals:   " ^ to_list (Core.Set.to_list machine.finals));
	print_endline (label "Transitions:");
	Core.Map.Poly.iteri machine.transitions ~f:(fun ~key:(state, _) ~data ->
		print_string "  ";
		print_transition state data;
		print_endline "");
	print_endline ""

let print_input_tape (tape: tape) =
	print_endline "│";
	print_string (blue ^ "Tape:     " ^ reset);
	print_tape tape;
	print_endline "";
	print_endline ""

let print_end_state (tape: tape) (state: state) =
	print_tape tape;
	print_string (gray ^ " >>>>> " ^ reset);
	print_endline (green ^ "Final state reached: " ^ bold ^ state ^ reset)

let print_current_state (tape: tape) (state: state) (v: transition) =
	print_tape tape;
	print_string (gray ^ " >>>>> " ^ reset);
	print_transition state v;
	print_endline ""

let print_step (step: time_data) =
	match step with
	| Accept (l, s) -> begin
			print_endline(
				"( " ^ string_of_int l ^
				" : " ^ string_of_int s ^ " )"
			) 
		end
	| Reject (l, s) -> begin
			print_endline(
					"( " ^ string_of_int l ^
					" : " ^ s ^ " )"
			)
		end
