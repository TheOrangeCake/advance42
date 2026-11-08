let match_1 : Names.rules =
	(
		Names.matchcase,
		[Encode.rule Encoding.transition_separator Names.match_read_1 Encoding.current_transition Types.Right]
	)

let match_read_1 : Names.rules =
	(
		Names.match_read_1,
		List.map (fun c -> Encode.rule c (Names.create_match_carry1_name c) c Types.Right) Encoding.universe
	)

let match_carry_1 : Names.rules list =
	let match_carry_1_c c : Names.rules =
		let name = Names.create_match_carry1_name c in
		(
			name,
			(Encode.rule Encoding.zone_delimiter (Names.create_match_search1_name c) Encoding.zone_delimiter Types.Left)::
			(Encode.sweep name (List.filter (fun x -> x <> Encoding.zone_delimiter) Encoding.tape_chars) Types.Right)
		)
	in List.map (fun c -> match_carry_1_c c) Encoding.universe

let match_search_1 : Names.rules list =
	let match_search_1_c c : Names.rules =
		(
			Names.create_match_search1_name c,
			(Encode.sweep (Names.create_match_cmp1_name c) Encoding.universe Types.Left)
		)
	in List.map (fun c -> match_search_1_c c) Encoding.universe

let match_compare_1 : Names.rules list =
	let cmp_c c : Names.rules =
		(
			Names.create_match_cmp1_name c,
			(Encode.rule c Names.match_back_eq c Types.Left)::
			(Encode.sweep Names.match_back_ne (List.filter (fun x -> x <> c) Encoding.universe) Types.Left)
		)
	in List.map (fun c -> cmp_c c) Encoding.universe

let match_back_eq : Names.rules =
	(
		Names.match_back_eq,
		(Encode.rule Encoding.current_transition Names.match_eq_r1 Encoding.current_transition Types.Right)::
		(Encode.sweep Names.match_back_eq (List.filter (fun c -> c <> Encoding.current_transition) Encoding.tape_chars) Types.Left)
	)

let match_back_ne : Names.rules =
	(
		Names.match_back_ne,
		(Encode.rule Encoding.current_transition Names.match_skip_transi Encoding.transition_separator Types.Right)::
		(Encode.sweep Names.match_back_ne (List.filter (fun c -> c <> Encoding.current_transition) Encoding.tape_chars) Types.Left)
	)

let match_skip_transi : Names.rules =
	(
		Names.match_skip_transi,
		(Encode.rule Encoding.transition_separator Names.match_read_1 Encoding.current_transition Types.Right)::
		(Encode.sweep Names.match_skip_transi ([Encoding.left ; Encoding.right] @ Encoding.universe) Types.Right)
	)

let match_eq_r1 : Names.rules =
	(
		Names.match_eq_r1,
		(Encode.sweep Names.match_read_2 Encoding.universe Types.Right)
	)

let match_read_2 : Names.rules =
	(
		Names.match_read_2,
		List.map (fun c -> Encode.rule c (Names.create_match_carry2_name c) c Types.Right) Encoding.universe
	)

let match_carry_2 : Names.rules list =
	let match_carry_2_c c : Names.rules =
		let name = Names.create_match_carry2_name c in
		(
			name,
			(Encode.rule Encoding.zone_delimiter (Names.create_match_cmp2_name c) Encoding.zone_delimiter Types.Left)::
			(Encode.sweep name (List.filter (fun x -> x <> Encoding.zone_delimiter) Encoding.tape_chars) Types.Right)
		)
	in List.map (fun c -> match_carry_2_c c) Encoding.universe

let match_compare_2 : Names.rules list =
	let cmp_c c : Names.rules =
		(
			Names.create_match_cmp2_name c,
			(Encode.rule c Names.match_back_found c Types.Left)::
			(Encode.sweep Names.match_back_ne (List.filter (fun x -> x <> c) Encoding.universe) Types.Left)
		)
	in List.map (fun c -> cmp_c c) Encoding.universe

let match_back_found : Names.rules =
	(
		Names.match_back_found,
		(Encode.rule Encoding.current_transition Names.apply Encoding.current_transition Types.Right)::
		(Encode.sweep Names.match_back_found (List.filter (fun c -> c <> Encoding.current_transition) Encoding.tape_chars) Types.Left)
	)
