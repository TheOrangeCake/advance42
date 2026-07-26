let ft_test_sign x = 
	if x >= 0
	then print_endline "positive"
	else print_endline "negative"

let test_suite =
	ft_test_sign 42;
	ft_test_sign 0;
	ft_test_sign (-42);
	ft_test_sign max_int;
	ft_test_sign min_int
