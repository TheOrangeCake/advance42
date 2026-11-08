let normal_mode (json: string) (input: string) =
	Printer.print_banner ();

	Parser.check_filepath_validity json;

	let machine = Machine.init_machine json in
	Printer.print_success ("Machine " ^ machine.name ^ " initialized !");
	Printer.print_machine machine;

	Parser.check_tape_validity input machine.alphabet machine.blank;

	let tape = Tape.init_tape input in
	Printer.print_success "Tape initialized !";
	Printer.print_input_tape tape;

	Printer.print_start ();
	Machine.run machine tape;

	Printer.print_message ""

let complexity_mode (json: string) (inputs_path: string) =
	Printer.print_banner ();

	Parser.check_filepath_validity json;

	let machine = Machine.init_machine json in
	Printer.print_success ("Machine " ^ machine.name ^ " initialized !");
	Printer.print_machine machine;

	let inputs = Parser.read_inputs inputs_path in
	if not (Estimator.enough_lengths (List.map String.length inputs))
		then begin
			Printer.print_error (
				"Need inputs of at least " ^ string_of_int Estimator.min_lengths ^
				" different lengths !"
			);
			exit 1
		end;
	List.iter (
		fun i ->
			Parser.check_tape_validity i machine.alphabet machine.blank
	) inputs;

	let tapes = List.map Tape.init_tape inputs in
	Printer.print_success "All tape initialized !";

	Printer.print_message "Starting time complexity estimation";
	Printer.print_message "";
	Printer.print_message "(input len : steps)";
	let steps = List.map (
		fun tape -> 
			let t_len = Tape.length tape in
			let step = Machine.run_complexity machine t_len tape in
			Printer.print_step step;
			step
	) tapes
	in
	Printer.print_message "";
	Estimator.estimate steps machine.name;

	Printer.print_message ""


let () =
	let argv = Sys.argv in
	let res = Parser.check_number_arguments_and_help argv in
	match res with
	| Normal (json, input) -> normal_mode json input
	| Complexity (json, inputs) -> complexity_mode json inputs
