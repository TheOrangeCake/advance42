let check_number_argument ?(len = 1) (argv: 'a array) =
	match Array.length argv with
	| n when n = len -> ()
	| _ ->
		Printer.print_error "Wrong number of arguments." ;
		Printer.print_usage_and_exit ("./machine_encoder <machine.json> <input>")

let check_filepath_validity filepath =
	if Sys.file_exists filepath then begin
		if Sys.is_directory filepath then begin
			Printer.print_error_and_exit ("'" ^ filepath ^ "' is a directory")
		end
	end else
		Printer.print_error_and_exit ("'" ^ filepath ^ "' does not exist")
