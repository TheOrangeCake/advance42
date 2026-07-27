let print_number n =
  if n < 10
    then
      print_int 0;
  print_int n

let ft_print_comb2 () =
  let b_max = 99 in
  let a_max = b_max - 1 in
  let rec loop_a a =
    if a <= a_max
      then let rec loop_b b =
        if b <= b_max
          then (
            print_number a;
            print_char ' ';
            print_number b;
            if a = a_max && b = b_max
              then
                print_char '\n'
              else (
                print_char ',';
                print_char ' ';
                loop_b (b + 1)
              )
            )
          else
            loop_a (a + 1)
        in loop_b (a + 1)
  in loop_a 0

(* let () =
  ft_print_comb2 () *)
