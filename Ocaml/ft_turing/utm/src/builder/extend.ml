let extend_tape_right : Names.rules =
	(
		Names.extend_tape_right,
		(Encode.rule Encoding.zone_delimiter Names.extend_skip_value Encoding.zone_delimiter Types.Left)::
		(Encode.sweep Names.extend_tape_right (List.filter (fun c -> c <> Encoding.zone_delimiter) Encoding.tape_chars) Types.Left)
	)

let extend_skip_value : Names.rules =
	(
		Names.extend_skip_value,
		(Encode.sweep Names.extend_skip_state Encoding.universe Types.Left)
	)

let extend_skip_state : Names.rules =
	(
		Names.extend_skip_state,
		(Encode.sweep Names.extend_skip_rule_sep Encoding.universe Types.Left)
	)

let extend_skip_rule_sep : Names.rules =
	(
		Names.extend_skip_rule_sep,
		[(Encode.rule Encoding.rule_separator Names.extend_read_blank Encoding.rule_separator Types.Left)]
	)

let extend_read_blank : Names.rules =
	(
		Names.extend_read_blank,
		List.map (fun c -> (Encode.rule c (Names.create_extend_carry_blank_name c) c Types.Right)) Encoding.universe
	)

let extend_carry_blank : Names.rules list =
	let extend_c c : Names.rules =
		let name = Names.create_extend_carry_blank_name c in
		(
			name,
			(Encode.rule Encoding.universal_blank Names.extend_rewind c Types.Left)::
			(Encode.sweep name (List.filter (fun x -> x <> Encoding.universal_blank) Encoding.tape_chars) Types.Right)
		)
	in List.map (fun c -> extend_c c) Encoding.universe

let extend_rewind : Names.rules =
	(
		Names.extend_rewind,
		(Encode.rule Encoding.universal_blank Names.find_hash Encoding.universal_blank Types.Right)::
		(Encode.sweep Names.extend_rewind (List.filter (fun c -> c <> Encoding.universal_blank) Encoding.tape_chars) Types.Left)
	)

let extend_tape_left : Names.rules =
	(
		Names.extend_tape_left,
		(Encode.sweep Names.extend_left_state Encoding.universe Types.Left)
	)

let extend_tape_left_state : Names.rules =
(
	Names.extend_left_state,
	(Encode.sweep Names.extend_left_sep Encoding.universe Types.Left)
)

let extend_left_sep : Names.rules =
	(
		Names.extend_left_sep,
		[(Encode.rule Encoding.rule_separator Names.extend_left_read_blank Encoding.rule_separator Types.Left)]
	)

let extend_left_read_blank : Names.rules =
	(
		Names.extend_left_read_blank,
		List.map (fun c -> (Encode.rule c (Names.create_extend_left_carry_blank_name c) c Types.Right)) Encoding.universe
	)

let extend_left_carry_blank : Names.rules list =
	let extend_c c : Names.rules =
		let name = Names.create_extend_left_carry_blank_name c in
		(
			name,
			(Encode.rule Encoding.head_position (Names.create_extend_left_loop_name c) Encoding.head_position Types.Right)::
			(Encode.sweep name (List.filter (fun x -> x <> Encoding.head_position) Encoding.tape_chars) Types.Right)
		)
	in List.map (fun c -> extend_c c) Encoding.universe

let extend_left_loop : Names.rules list =
	let extend_c c : Names.rules =
		(
			Names.create_extend_left_loop_name c,
			(Encode.rule Encoding.universal_blank Names.apply_restore c Types.Left)::
			(List.map (fun x -> (Encode.rule x (Names.create_extend_left_loop_name x) c Types.Right)) Encoding.universe)
		)
	in List.map (fun c -> extend_c c) Encoding.universe
