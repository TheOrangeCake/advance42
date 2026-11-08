let () =
	let argv = Sys.argv in
	Parser.check_number_argument argv ~len:3;
	let filepath = (Array.get argv 1) in
	Parser.check_filepath_validity filepath ;
	let input = Array.get argv 2 in
	let machine = Machine_parser.init_machine filepath in
	let encoded = (Encode.encode_machine machine) ^ input in
	let illegal = Core.Set.inter Encoding.get_every_reserved_char (Core.Char.Set.of_list (Core.String.to_list input)) in
	if Core.Set.is_empty illegal then begin
		Machine_parser.check_tape_validity input machine.alphabet machine.blank ;
		print_endline encoded
	end else
		Printer.print_error_and_exit ("Some characters in the INPUT are reserved by the UTM: " ^ (Core.Char.Set.sexp_of_t illegal |> Core.Sexp.to_string))
