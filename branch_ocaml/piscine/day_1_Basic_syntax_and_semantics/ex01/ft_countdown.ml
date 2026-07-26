let rec ft_countdown x =
	if x <= 0
	then (
		print_int 0;
		print_char '\n'
	) else (
		print_int x;
		print_char '\n';
		ft_countdown(x - 1)
	)

let () =
	ft_countdown 5;
	print_char '\n';
	ft_countdown 0;
	print_char '\n';
	ft_countdown (-10)
