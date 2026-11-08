open Types
open Printer

let extract_result_or_exit res =
	let print_err_and_exit str err =
		print_error (str ^ err) ; exit 1
	in match res with
	| Ok value -> value
	| Error (Json_error err) -> print_err_and_exit "JSON error: " err
	| Error (Missing_field err) -> print_err_and_exit "Missing field: " err
	| Error (Invalid_symbol err) -> print_err_and_exit "Invalid symbol: " err
	| Error (Blank_not_in_alphabet) -> print_err_and_exit "Blank: " "Not in alphabet"
	| Error (Unknown_state err) -> print_err_and_exit "State: " err
	| Error (Initial_not_in_states) -> print_err_and_exit "Initial: " "Not in states"
	| Error (Finals_not_subset_of_states) -> print_err_and_exit "Finals: " "Not a subset of states"
	| Error (Duplicate_transitions (str, char)) -> print_err_and_exit ("Transitions: Duplicate of " ^ str ^ " when reading the symbol ") (String.make 1 char)
	| Error _ -> print_err_and_exit "Default error" ""

let check_tape_validity (input: string) (alphabet: symbol_set) (blank: symbol) =
	if Core.String.is_empty input then begin
		print_error "Tape can't be empty !";
		exit 1
	end else
		match Core.String.find input ~f:(
			fun c -> not (Core.Set.mem alphabet c) || Core.Char.equal c blank
		) with
		| None -> ()
		| Some c -> begin
			if Core.Char.equal c blank then
				print_error ("Tape can't contain blank character " ^ String.make 1 c)
			else
				print_error ("'" ^ String.make 1 c ^ "' is not in character set !");
			exit 1
		end

let check_filepath_validity filepath =
	if Sys.file_exists filepath then begin
		if Sys.is_directory filepath then begin
			print_error ("'" ^ filepath ^ "' is a directory");
			exit 1
		end
	end else begin
		print_error ("'" ^ filepath ^ "' does not exist");
		exit 1
	end

let extract_json filepath =
	try
		Result.ok (Yojson.Safe.from_file filepath)
	with | _ -> Result.error (Json_error "occured when reading the file")

let get_name json =
	try
		Result.ok (Yojson.Safe.Util.(json |> member "name" |> to_string))
	with _ -> Result.error (Missing_field "name")

let get_alphabet json =
	let alpha = try
		Result.ok Yojson.Safe.Util.(json |> member "alphabet" |> (convert_each to_string))
	with _ -> Result.error (Missing_field "alphabet") in
	match alpha with
	| Error err -> Result.error err
	| Ok value ->
		if Core.List.for_all value ~f:(fun a -> Core.String.length a == 1) == false then
			Result.error (Invalid_symbol "Every symbol in alphabet must be exactly 1 character")
		else
			Result.ok (Core.Char.Set.of_list (Core.List.map value ~f:(fun c -> c.[0])))

let get_blank json =
	let blank = try
		Result.ok Yojson.Safe.Util.(json |> member "blank" |> to_string)
	with _ -> Result.error (Missing_field "blank") in
	match blank with
	| Error err -> Result.error (err)
	| Ok value -> begin
		match get_alphabet json with
		| Error _ -> Result.error (Default_error "occured while retrieving the alphabet")
		| Ok alpha ->
			let value = value.[0] in
			if Core.Set.mem alpha value == false then
				Result.error (Blank_not_in_alphabet)
			else
				Result.ok value
	end

let get_states json = try
		Result.ok (Core.String.Set.of_list Yojson.Safe.Util.(json |> member "states" |> (convert_each to_string)))
	with _ -> Result.error (Missing_field "states")

let get_initial json =
	let initial = try
		Result.ok Yojson.Safe.Util.(json |> member "initial" |> to_string)
	with _ -> Result.error (Missing_field "initial") in
	match initial with
	| Ok value -> begin
		match get_states json with
		| Error _ -> Result.error (Default_error "occured when retrieving the states")
		| Ok states ->
			if Core.Set.mem states value == false then
				Result.error (Initial_not_in_states)
			else
				Result.ok value
	end
	| Error _ -> initial

let get_finals json =
	let finals = try
		Result.ok Yojson.Safe.Util.(json |> member "finals" |> (convert_each to_string))
	with _ -> Result.error (Missing_field "finals") in
	match finals with
	| Error err -> Result.error err
	| Ok value -> begin
		match get_states json with
		| Error _ -> Result.error (Default_error "occured when retrieving the states")
		| Ok states ->
			let values = Core.String.Set.of_list value in
			if Core.Set.is_subset values ~of_:states == false then
				Result.error (Finals_not_subset_of_states)
			else
				Result.ok values
	end

let parse_transition json =
	try
		Yojson.Safe.Util.([
			("read"		, json |> member "read"		|> to_string) ;
			("to_state"	, json |> member "to_state"	|> to_string) ;
			("write"	, json |> member "write"	|> to_string) ;
			("action"	, json |> member "action"	|> to_string)])
	with _ -> (prerr_endline "Error in json: transition: A key miss or was mistyped" ; exit 1)

let check_transitions_state_keys transitions states =
	if Core.List.for_all transitions ~f:(fun (key, _) -> Core.Set.mem states key) == false then
		Result.error (Unknown_state "The transitions states must exist in the states")
	else
		Result.ok (())

let rec check_transitions_state_value lst alphabet states =
	match lst with
	| [] -> Result.ok (())
	| head::tail -> begin
		let rec check_transition_values l alphabet states =
			match l with
			| [] -> check_transitions_state_value tail alphabet states
			| (read, value)::t when Core.String.equal read "read" ->
				if Core.String.length value == 1 then begin
					let value = value.[0] in
					if Core.Set.mem alphabet value == false then
						Result.error (Invalid_symbol "The read symbol must exist in the alphabet")
					else
						check_transition_values t alphabet states
				end else
					Result.error (Invalid_symbol "The read symbol must be only 1 character")
			| (state, value)::t when Core.String.equal state "to_state" ->
				if Core.Set.mem states value == false then
					Result.error (Unknown_state "The to_state value must be a valid state")
				else
					check_transition_values t alphabet states
			| (write, value)::t when Core.String.equal write "write" ->
				if Core.String.length value == 1 then begin
					let value = value.[0] in
					if Core.Set.mem alphabet value == false then
						Result.error (Invalid_symbol "The write symbol must exist in the alphabet")
					else
						check_transition_values t alphabet states
				end else
					Result.error (Invalid_symbol "The write symbol must be only 1 character")
			| (action, value)::t when Core.String.equal action "action" ->
				if Core.String.equal value "LEFT" == false && Core.String.equal value "RIGHT" == false then
					Result.error (Invalid_symbol "The action value must be LEFT or RIGHT")
				else
					check_transition_values t alphabet states
			| _ -> begin
				Result.error (Default_error "")
			end
		in check_transition_values head alphabet states
	end

let field name fields =
	match Core.List.Assoc.find fields ~equal:Core.String.equal name with
	| Some v -> v
	| None -> assert false

let action_of_string x =
	match x with
	| "LEFT"  -> Left
	| "RIGHT" -> Right
	| _ -> assert false

let transition_of_fields fields =
{
	read     = (field "read" fields).[0];
	to_state = field "to_state" fields;
	write    = (field "write" fields).[0];
	action   = action_of_string (field "action" fields)
}

let build_transitions value =
	let entries =
		Core.List.concat_map value ~f:(fun (state, rules) ->
			Core.List.map rules ~f:(fun fields ->
				let tr = transition_of_fields fields in
				((state, tr.read), tr)))
	in
	match Core.Map.Poly.of_alist entries with
	| `Duplicate_key (state, c) -> Result.error (Duplicate_transitions (state, c))
	| `Ok m -> Result.ok m

let get_transitions json =
	let transitions = try
		Result.ok Yojson.Safe.Util.(
			json |> member "transitions" |> to_assoc |>
			List.map (fun (state, rules_json) -> (state, convert_each parse_transition rules_json)))
	with _ -> Result.error (Missing_field "transitions") in
	match transitions with
	| Error err -> Result.error err
	| Ok value -> begin
		match get_states json with
		| Error _ -> Result.error (Default_error "occured when retrieving the states")
		| Ok states -> begin
			match get_alphabet json with
			| Error _ -> Result.error (Default_error "occured when retrieving the states")
			| Ok alphabet-> begin
				match check_transitions_state_keys value states with
				| Error err -> Result.error err
				| Ok _ -> begin
					match check_transitions_state_value (Core.List.concat_map value ~f:snd) alphabet states with
					| Error err -> Result.error err
					| Ok _ -> build_transitions value
				end
			end
		end
	end

let init_machine(filepath: string) : turing =
	let json = extract_result_or_exit (extract_json filepath) in
	let name = extract_result_or_exit (get_name json) in
	let alphabet = extract_result_or_exit (get_alphabet json) in
	let blank = extract_result_or_exit (get_blank json) in
	let states = extract_result_or_exit (get_states json) in
	let initial = extract_result_or_exit (get_initial json) in
	let finals = extract_result_or_exit (get_finals json) in
	let transitions = extract_result_or_exit (get_transitions json) in
	{
		name = name;
		alphabet = alphabet;
		blank = blank;
		states = states;
		initial = initial;
		finals = finals;
		transitions = transitions
	}