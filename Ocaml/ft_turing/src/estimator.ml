open Types

let min_lengths = 3

(* True if there are at least min_lengths different lengths *)
let enough_lengths (lengths: tape_len list) : bool =
  List.length (List.sort_uniq compare lengths) >= min_lengths

let clean_data (data: time_data list) : time list =
  let filtered = List.filter_map (
    fun step -> match step with
    | Reject _ -> None
    | Accept s -> Some s
  ) data
  in
  if not (enough_lengths (List.map fst filtered))
    then begin
      Printer.print_error (
        "Need inputs of at least " ^ string_of_int min_lengths ^
        " different lengths after filtered !"
      );
      exit 1
    end;
  let sorted = List.sort (
    fun (l1, s1) (l2, s2) ->
      if l1 <> l2 then compare l1 l2 else compare s2 s1
  ) filtered
  in
  Core.List.remove_consecutive_duplicates
    ~which_to_keep:`First
    ~equal:(fun (l1, _) (l2, _) -> l1 = l2) sorted

(* data is (f(n), steps) *)
(*
  Normal Equation Formula:
  m = sum( (f(n)_i - avg_f(n)) (steps_i - avg_steps) ) /
      sum( (f(n)_i - avg_f(n))^2 )
  b = avg_steps - m * avg_f(n)
*)
(* None if f(n) overflows (or is -inf) *)
let linear_regression (data: (float * float) list) : (float * float) option =
  let avg_fn = List.fold_left (
    fun acc (fn, _) -> acc +. fn
    ) 0. data /. float_of_int (List.length data)
  in
  let avg_steps = List.fold_left (
    fun acc (_, step) -> acc +. step
  ) 0. data /. float_of_int (List.length data)
  in
  let numerator = List.fold_left (
    fun acc (fn, step) ->
      acc +. (fn -. avg_fn) *. (step -. avg_steps)
  ) 0. data
  in
  let denominator = List.fold_left (
    fun acc (fn, _) ->
      acc +. (fn -. avg_fn) ** 2.
  ) 0. data
  in
  let scale = List.fold_left (
    fun acc (fn, _) -> acc +. fn *. fn
  ) 0. data
  in
  if not (Float.is_finite scale && Float.is_finite denominator) then None
  else if denominator <= Float.epsilon *. scale then Some (0., avg_steps)
  else
    let m = numerator /. denominator in
    let b = avg_steps -. m *. avg_fn in
    Some (m, b)


(* data is (f(n), steps) *)
(*
  Formula:
  sum square error = sum( (real step - predicted step) ^ 2 )
*)
let compute_error (data: (float * float) list) (thetas: (float * float)) : float =
  let m = fst thetas in
  let b = snd thetas in
  let sum_error = List.fold_left (
    fun acc (fn, step) -> acc +. (step -. (m *. fn +. b)) ** 2. 
  ) 0. data
  in
  let avg_error = sum_error /. float_of_int (List.length data) in
  Float.sqrt avg_error

(* A linear function is y = mx + b; We try to find the best fit m and b *)
let estimate (data: time_data list) (name: string) =
  let cleaned = clean_data data
  in
  let complexities = List.filter_map (
    fun c ->
      (* set f(n) and convert step to float *)
      let c_set = List.map (
        fun (len, steps) ->
          (Complexity.transform c (float_of_int len), float_of_int steps)
      ) cleaned
      in
      Printer.print_message (
        "Analysing " ^ Complexity.string_of_complexity c ^ " case..."
      );
      match linear_regression c_set with
      | None ->
          Printer.print_message (
            "Case " ^ Complexity.string_of_complexity c ^ " skipped: values overflow"
          );
          None
      | Some (m, b) ->
          let error = compute_error c_set (m, b) in
          Printer.print_message ("-> Error is " ^ string_of_float error);
          Some {
            bigO = c;
            m = m;
            b = b;
            error = error
          }
  ) Complexity.all
  in
  match complexities with
  | [] -> Printer.print_error "No complexity could be estimated !"
  | h :: t ->
    let best = List.fold_left (
      fun best c -> if c.error < best.error then c else best
    ) h t
    in
    Printer.print_success (
      "Estimated complexity for " ^ name ^ " is: " ^ Complexity.string_of_complexity best.bigO ^
      " (Error " ^ string_of_int (int_of_float best.error) ^ " step(s))"
    )
