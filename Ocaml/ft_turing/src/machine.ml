open Types

let init_machine(filepath: string) : turing =
	let json = Parser.extract_result_or_exit (Parser.extract_json filepath) in
	let name = Parser.extract_result_or_exit (Parser.get_name json) in
	let alphabet = Parser.extract_result_or_exit (Parser.get_alphabet json) in
	let blank = Parser.extract_result_or_exit (Parser.get_blank json) in
	let states = Parser.extract_result_or_exit (Parser.get_states json) in
	let initial = Parser.extract_result_or_exit (Parser.get_initial json) in
	let finals = Parser.extract_result_or_exit (Parser.get_finals json) in
	let transitions = Parser.extract_result_or_exit (Parser.get_transitions json) in
	{
		name = name;
		alphabet = alphabet;
		blank = blank;
		states = states;
		initial = initial;
		finals = finals;
		transitions = transitions
	}

let run (machine: turing) (input_tape: tape) =
	let rec loop tape state =
		if Core.Set.mem machine.finals state then begin
			Printer.print_end_state tape state
		end else
			let transition = Core.Map.find machine.transitions (state, tape.head)
			in match transition with
			| None -> Printer.print_error (
					"Machine blocked: no transition for (" ^
					state ^ ", " ^ String.make 1 tape.head ^ ")"
				); exit 1
			| Some v -> begin
					Printer.print_current_state tape state v;
					let tape = { tape with head = v.write } in
					let updated_tape = match v.action with
						| Left -> Tape.move_left tape machine.blank
						| Right -> Tape.move_right tape machine.blank
					in
					loop updated_tape v.to_state
				end
	in loop input_tape machine.initial


let time_limit = 10.
let check_interval = 1_000_000
let max_steps = 20_000_000

let run_complexity (machine: turing) (t_len: tape_len) (input_tape: tape) : time_data =
	let deadline = Sys.time () +. time_limit in

	let rec loop tape state acc =
		if Core.Set.mem machine.finals state then Accept (t_len, acc)
		else if acc >= max_steps then Reject (t_len, "Step limit")
		else
			if acc mod check_interval = 0 && Sys.time () > deadline
				then Reject (t_len, "Time out")
			else
				let transition = Core.Map.find machine.transitions (state, tape.head)
				in match transition with
				| None -> Reject (t_len, "Blocked")
				| Some v -> begin
						let tape = { tape with head = v.write } in
						let updated_tape = match v.action with
							| Left -> Tape.move_left tape machine.blank
							| Right -> Tape.move_right tape machine.blank
						in
						loop updated_tape v.to_state (acc + 1)
					end
	in loop input_tape machine.initial 0
