let rec tak x y z =
  if y < x
    then tak
      (tak (x - 1) y z)
      (tak (y - 1) z x)
      (tak (z - 1) x y)
  else
    z

(* let () =
    assert (tak 1 2 3 = 3);
    assert (tak 5 23 7 = 7);
    assert (tak 9 1 0 = 1);
    assert (tak 1 1 1 = 1);
    assert (tak 0 42 0 = 0);
    assert (tak 23498 98734 98776 = 98776);
    assert (tak 18 12 6 = 7) *)
