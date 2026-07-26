let ft_print_comb () =
  let max_c = 9 in
  let max_b = max_c - 1 in
  let max_a = max_b - 1 in
  let rec increase_a a =
    if a <= max_a
    then (
      let rec increase_b b =
        if b <= max_b
        then (
          let rec increase_c c = (
            if c <= max_c
            then (
              print_int a;
              print_int b;
              print_int c;
              if (a = max_a && b = max_b && c = max_c)
              then
                print_string("\n")
              else (
                print_string ", ";
                increase_c (c + 1)
              )
            ) else (
              increase_b (b + 1)
            )
          )
          in increase_c (b + 1)
        ) else (
          increase_a (a + 1)
        )
      in increase_b (a + 1)
    )
    in increase_a 0

let () =
  ft_print_comb()
