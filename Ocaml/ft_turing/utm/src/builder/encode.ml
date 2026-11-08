let extract_action action =
	match action with
	| Types.Left -> "LEFT"
	| Types.Right -> "RIGHT"

let rule read to_state write action : Yojson.Safe.t = `Assoc [("read", `String (Core.String.of_char read));("to_state", `String to_state);("write", `String (Core.String.of_char write));("action", `String (extract_action action))]

let sweep state_name characters direction =
	List.map (fun c -> rule c state_name c direction) characters