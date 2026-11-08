let apply : Names.rules =
	(
		Names.apply,
		(Encode.sweep Names.apply_skip_1 Encoding.universe Types.Right)
	)

let apply_skip_1 : Names.rules =
	(
		Names.apply_skip_1,
		(Encode.sweep Names.apply_read_state Encoding.universe Types.Right)
	)

let apply_read_state : Names.rules =
	(
		Names.apply_read_state,
		List.map (fun c -> (Encode.rule c (Names.create_apply_carry_state_name c) c Types.Right)) Encoding.universe
	)

let apply_carry_state : Names.rules list =
	let carry_state_c c : Names.rules =
		let name = Names.create_apply_carry_state_name c in
		(
			name,
			(Encode.rule Encoding.zone_delimiter (Names.create_apply_search_state_name c) Encoding.zone_delimiter Types.Left)::
			(Encode.sweep name (List.filter (fun x -> x <> Encoding.zone_delimiter) Encoding.tape_chars) Types.Right)
		)
	in List.map (fun c -> carry_state_c c) Encoding.universe

let apply_search_state : Names.rules list =
	let apply_search_1_c c : Names.rules =
		(
			Names.create_apply_search_state_name c,
			(Encode.sweep (Names.create_apply_put_state_name c) Encoding.universe Types.Left)
		)
	in List.map (fun c -> apply_search_1_c c) Encoding.universe

let apply_put_state : Names.rules list =
	let apply_put_c c : Names.rules =
		(
			Names.create_apply_put_state_name c,
			List.map (fun x -> (Encode.rule x Names.apply_back c Types.Left)) Encoding.universe
		)
	in List.map (fun c -> apply_put_c c) Encoding.universe

let apply_back : Names.rules =
	(
		Names.apply_back,
		(Encode.rule Encoding.current_transition Names.apply_back_1 Encoding.current_transition Types.Right)::
		(Encode.sweep Names.apply_back (List.filter (fun c -> c <> Encoding.current_transition) Encoding.tape_chars) Types.Left)
	)

let apply_back_1 : Names.rules =
	(
		Names.apply_back_1,
		(Encode.sweep Names.apply_back_2 Encoding.universe Types.Right)
	)

let apply_back_2 : Names.rules =
	(
		Names.apply_back_2,
		(Encode.sweep Names.apply_back_3 Encoding.universe Types.Right)
	)

let apply_back_3 : Names.rules =
	(
		Names.apply_back_3,
		(Encode.sweep Names.apply_read_write Encoding.universe Types.Right)
	)

let apply_read_write : Names.rules =
	(
		Names.apply_read_write,
		List.map (fun c -> (Encode.rule c (Names.create_apply_read_write_name c) c Types.Right)) Encoding.universe
	)

let apply_read_c_dir : Names.rules list =
	let read_c c =
		(
			Names.create_apply_read_write_name c,
			List.map (fun x -> (Encode.rule x (Names.create_apply_carry_c_dir_name x c) x Types.Right)) [Encoding.left ; Encoding.right]
		)
	in List.map (fun c -> read_c c) Encoding.universe

let apply_right : Names.rules list =
	let carry_c c =
		let name = Names.create_apply_carry_c_dir_name Encoding.right c in
		(
			name,
			(Encode.rule Encoding.head_position (Names.apply_replace_head_right) c Types.Right)::
			(Encode.sweep name (List.filter (fun x -> x <> Encoding.head_position) Encoding.tape_chars) Types.Right)
		)
	in List.map (fun c -> carry_c c) Encoding.universe

let apply_replace_head_right : Names.rules =
	(
		Names.apply_replace_head_right,
		List.map (fun c -> (Encode.rule c Names.apply_restore Encoding.head_position Types.Left)) Encoding.universe
	)

let apply_restore : Names.rules =
	(
		Names.apply_restore,
		(Encode.rule Encoding.current_transition Names.apply_rewind Encoding.transition_separator Types.Left)::
		(Encode.sweep Names.apply_restore (List.filter (fun c -> c <> Encoding.current_transition) Encoding.tape_chars) Types.Left)
	)

let apply_rewind : Names.rules =
	(
		Names.apply_rewind,
		(Encode.rule Encoding.universal_blank Names.check_hash Encoding.universal_blank Types.Right)::
		(Encode.sweep Names.apply_rewind (List.filter (fun c -> c <> Encoding.universal_blank) Encoding.tape_chars) Types.Left)
	)

let apply_left : Names.rules list =
	let carry_c c =
		let name = Names.create_apply_carry_c_dir_name Encoding.left c in
		(
			name,
			(Encode.rule Encoding.head_position (Names.create_apply_replace_head_left_name c) Encoding.head_position Types.Right)::
			(Encode.sweep name (List.filter (fun x -> x <> Encoding.head_position) Encoding.tape_chars) Types.Right)
		)
	in List.map (fun c -> carry_c c) Encoding.universe

let apply_replace_left : Names.rules list =
	let replace_left_c c : Names.rules =
		let name = Names.create_apply_replace_head_left_name c in
		(
			name,
			List.map (fun x -> (Encode.rule x Names.apply_swap_0 c Types.Left)) Encoding.universe
		)
	in List.map (fun c -> replace_left_c c) Encoding.universe

let apply_swap_0 : Names.rules =
	(
		Names.apply_swap_0,
		[(Encode.rule Encoding.head_position Names.apply_swap_1 Encoding.head_position Types.Left)]
	)

let apply_swap_1 : Names.rules =
	(
		Names.apply_swap_1,
		(Encode.rule Encoding.zone_delimiter Names.extend_tape_left Encoding.zone_delimiter Types.Left)::
		(List.map (fun c -> (Encode.rule c (Names.create_apply_swap_t_name c) Encoding.head_position Types.Right)) Encoding.universe)
	)

let apply_swapt : Names.rules list =
	let swap_t_c c : Names.rules =
		(
			Names.create_apply_swap_t_name c,
			[(Encode.rule Encoding.head_position Names.apply_restore c Types.Left)]
		)
	in List.map (fun c -> swap_t_c c) Encoding.universe
