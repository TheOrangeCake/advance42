let utm_json ~alphabet ~blank ~initial (states : (string * Yojson.Safe.t list) list) : Yojson.Safe.t =
	let strings l = `List (List.map (fun s -> `String s) l) in
	let defined = List.map fst states in
	let targets =
		List.concat_map
		(fun (_, rules) ->
			List.map (fun r -> Yojson.Safe.Util.(r |> member "to_state" |> to_string)) rules)
		states
	in
	let all_states = List.sort_uniq compare ([Names.final] @ defined @ targets) in
	`Assoc
		[ "name", `String "utm"
		; "alphabet", strings (List.map (String.make 1) alphabet)
		; "blank", `String (String.make 1 blank)
		; "states", strings all_states
		; "initial", `String initial
		; "finals", strings [Names.final]
		; "transitions", `Assoc (List.map (fun (n, r) -> (n, `List r)) states)
		]

let () = 
	print_endline (
		Yojson.Safe.to_string
		(utm_json
			~alphabet:Encoding.allowed_chars
			~blank:'~'
			~initial: Names.check_hash
			(
				[Find.find_hash ; Find.seek ; Find.read ; Find.rewind] @
				Find.carry @ Find.put @

				[Match.match_1 ; Match.match_read_1; Match.match_back_eq ; Match.match_back_ne ; Match.match_skip_transi] @
				Match.match_carry_1 @ Match.match_search_1 @ Match.match_compare_1 @
				[Match.match_eq_r1 ; Match.match_read_2 ; Match.match_back_found] @
				Match.match_carry_2 @ Match.match_compare_2 @

				[Apply.apply ; Apply.apply_skip_1 ; Apply.apply_read_state ; Apply.apply_back ; Apply.apply_back_1 ;
				Apply.apply_back_2 ; Apply.apply_back_3 ; Apply.apply_read_write ; Apply.apply_replace_head_right ;
				Apply.apply_restore ; Apply.apply_rewind ; Apply.apply_swap_0 ; Apply.apply_swap_1] @

				Apply.apply_carry_state @ Apply.apply_search_state @
				Apply.apply_put_state @ Apply.apply_read_c_dir @
				Apply.apply_replace_left @
				Apply.apply_right @ Apply.apply_left @ Apply.apply_swapt @

				[Check.check_hash ; Check.check_value ; Check.check_state ; Check.check_rewind] @
				Check.check_skip_state @ Check.check_1 @ Check.check_2 @ Check.check_final @

				[Extend.extend_tape_right ; Extend.extend_skip_value ; Extend.extend_skip_state ; Extend.extend_skip_rule_sep ; Extend.extend_read_blank ; Extend.extend_rewind] @
				Extend.extend_carry_blank @
				[Extend.extend_tape_left ; Extend.extend_tape_left_state ; Extend.extend_left_sep ; Extend.extend_left_read_blank] @
				Extend.extend_left_carry_blank @ Extend.extend_left_loop
			)
		)
	)
