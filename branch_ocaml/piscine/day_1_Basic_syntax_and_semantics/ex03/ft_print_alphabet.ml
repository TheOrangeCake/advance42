let ft_print_alphabet () =
  let first = int_of_char 'a' in
  let last = int_of_char 'z' in
  let rec alphabet current =
    if current <> last + 1
    then (
      print_char(char_of_int current);
      alphabet (current + 1)
    ) in
    alphabet first;
    print_char '\n'

let () =
  ft_print_alphabet()
