let universal_blank = '~'
let rule_separator = '|'
let transition_separator = ';'
let zone_delimiter = '#'
let head_position = '^'
let left = 'L'
let right = 'R'
let current_transition = ' '

let allowed_chars = Core.String.to_list "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!$%&'()* +,-./:<=>?@[]_`{}^#;|~"
let reserved_chars = [
		universal_blank ;
		rule_separator ;
		transition_separator ;
		zone_delimiter ;
		head_position ;
		left ;
		right ;
		current_transition
	]
let get_every_reserved_char = Core.Char.Set.of_list reserved_chars
let universe = List.filter (fun c -> not (List.mem c reserved_chars)) allowed_chars
let tape_chars = universe @ List.filter (fun c -> c <> universal_blank) reserved_chars
