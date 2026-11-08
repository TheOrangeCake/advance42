let check_hash : Names.rules =
	(
		Names.check_hash,
		(Encode.rule Encoding.zone_delimiter Names.check_value Encoding.zone_delimiter Types.Left)::
		(Encode.sweep Names.check_hash (List.filter (fun c -> c <> Encoding.zone_delimiter) Encoding.tape_chars) Types.Right)
	)

let check_value : Names.rules =
	(
		Names.check_value,
		(Encode.sweep Names.check_state Encoding.universe Types.Left)
	)

let check_state : Names.rules =
	(
		Names.check_state,
		List.map (fun c -> (Encode.rule c (Names.create_check_value_name c) c Types.Left)) Encoding.universe
	)

let check_skip_state : Names.rules list =
	let check_skip_state_c c : Names.rules =
		(
			Names.create_check_value_name c,
			[(Encode.rule Encoding.rule_separator (Names.create_check_1_c_name c) Encoding.rule_separator Types.Left)]
		)
	in List.map (fun c -> check_skip_state_c c) Encoding.universe

let check_1 : Names.rules list =
	let check_1_c c : Names.rules =
		(
			Names.create_check_1_c_name c,
			(Encode.sweep (Names.create_check_2_c_name c) Encoding.universe Types.Left)
		)
	in List.map (fun c -> check_1_c c) Encoding.universe
let check_2 : Names.rules list =
	let check_2_c c : Names.rules =
		(
			Names.create_check_2_c_name c,
			[(Encode.rule Encoding.rule_separator (Names.create_check_final_c_name c) Encoding.rule_separator Types.Left)]
		)
	in List.map (fun c -> check_2_c c) Encoding.universe

let check_final : Names.rules list =
	let check_final_c c : Names.rules =
		let name = Names.create_check_final_c_name c in
		(
			name,
			(Encode.rule c Names.final c Types.Left)::
			(Encode.rule Encoding.rule_separator Names.check_rewind Encoding.rule_separator Types.Left)::
			(Encode.sweep name (List.filter (fun x -> x <> c) Encoding.universe) Types.Left)
		)
	in List.map (fun c -> check_final_c c) Encoding.universe

let check_rewind : Names.rules =
	(
		Names.check_rewind,
		(Encode.rule Encoding.universal_blank Names.find_hash Encoding.universal_blank Types.Right)::
		(Encode.sweep Names.check_rewind (List.filter (fun c -> c <> Encoding.universal_blank) Encoding.tape_chars) Types.Left)
	)
