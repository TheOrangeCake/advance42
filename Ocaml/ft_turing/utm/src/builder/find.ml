let find_hash : Names.rules =
	let characters = List.filter (fun c -> c <> Encoding.zone_delimiter) Encoding.tape_chars in
	(
		Names.find_hash,
		(Encode.rule Encoding.zone_delimiter Names.seek Encoding.zone_delimiter Types.Right)::(Encode.sweep Names.find_hash characters Types.Right)
	)

let seek : Names.rules =
	(
		Names.seek,
		(Encode.rule Encoding.head_position Names.read Encoding.head_position Types.Right)::
		(Encode.sweep Names.seek (List.filter (fun c -> c <> Encoding.head_position) Encoding.universe) Types.Right)
	)

let read : Names.rules =
	(
		Names.read,
		(Encode.rule Encoding.universal_blank Names.extend_tape_right Encoding.universal_blank Types.Left)::
		(List.map (fun c -> (Encode.rule c (Names.create_carry_name c) c Types.Left)) Encoding.universe)
	)

let carry : Names.rules list =
	let carry_c c : Names.rules =
		let carry_name = Names.create_carry_name c in
		(
			carry_name,
			(Encode.rule Encoding.zone_delimiter (Names.create_put_name c) Encoding.zone_delimiter Types.Left)::
			(Encode.rule Encoding.head_position carry_name Encoding.head_position Types.Left)::
			(Encode.sweep carry_name (List.filter (fun c -> c <> Encoding.zone_delimiter) Encoding.universe) Types.Left)
		)
	in List.map (fun c -> carry_c c) Encoding.universe

let put : Names.rules list =
	let put_c c : Names.rules =
		(
			Names.create_put_name c,
			List.map (fun x -> Encode.rule x Names.rewind c Types.Left) Encoding.universe
		)
	in List.map (fun c -> put_c c) Encoding.universe

let rewind : Names.rules =
	(
		Names.rewind,
		(Encode.rule Encoding.universal_blank Names.matchcase Encoding.universal_blank Types.Right)::
		(Encode.sweep Names.rewind Encoding.tape_chars Types.Left)
	)
