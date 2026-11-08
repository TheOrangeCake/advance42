let print_error err =
	prerr_endline ("Error: " ^ err)

let print_error_and_exit err =
	prerr_endline ("Error: " ^ err) ;
	exit 1

let print_usage usage =
	prerr_endline ("Usage: " ^ usage)

let print_usage_and_exit usage =
	prerr_endline ("Usage: " ^ usage) ;
	exit 1
