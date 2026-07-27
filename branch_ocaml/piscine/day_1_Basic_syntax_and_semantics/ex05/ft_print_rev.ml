let ft_print_rev str =
  let len = String.length str in
  let rec rev i =
    if i >= 0
      then (
        print_char(String.get str i);
        rev (i - 1)
      )
  in rev (len - 1);
  print_char '\n'

(* let () =
  ft_print_rev "Hello world !";
  ft_print_rev "Evaluation";
  ft_print_rev "From 42 Lausanne";
  ft_print_rev "" *)
