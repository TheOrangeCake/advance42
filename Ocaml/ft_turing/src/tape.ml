open Types

let init_tape (expr: string) =
  let expr_list = Core.String.to_list expr in
  match expr_list with
  | [] -> assert false
  | head::tail -> { left = [] ; head = head ; right = tail }

let move_left (t: tape) (blank: symbol) =
  match t.left with
  | [] -> { left = [] ; head = blank ; right = t.head::t.right }
  | curr::tail -> { left = tail ; head = curr ; right = t.head::t.right }

let move_right (t: tape) (blank: symbol) =
  match t.right with
  | [] -> { left = t.head::t.left ; head = blank ; right = [] }
  | curr::tail -> { left = t.head::t.left ; head = curr ; right = tail }

let length (t: tape) : tape_len =
  let rec count tape acc =
    match tape with
    | [] -> acc
    | h :: t -> count t (acc + 1)
  in 
  let left_len = count t.left 0 in
  let right_len = count t.right 0 in
  left_len + 1 + right_len
