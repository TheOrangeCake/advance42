let rec fibonacci n =
  if n < 0
    then -1
  else if n = 0 || n = 1
    then n
  else
    fibonacci (n - 2) + fibonacci (n - 1)

let () =
    assert (fibonacci (-42) = -1);
    assert (fibonacci 0 = 0);
    assert (fibonacci 1 = 1);
    assert (fibonacci 3 = 2);
    assert (fibonacci 6 = 8);
