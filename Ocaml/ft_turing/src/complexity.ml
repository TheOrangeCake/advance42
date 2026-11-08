open Types

let rec factorial (n: float) : float =
  if n <= 1. then 1.
  else factorial (n -. 1. ) *. n

let transform (c: bigO) (n: float) : float =
  match c with
  | Constant -> 1.
  | Logarithmic -> log n
  | Linear -> n
  | Linearithmic -> n *. log n
  | Quadratic -> n ** 2.
  | Cubic -> n ** 3.
  | Exponential -> 2. ** n
  | Factorial -> factorial n

let string_of_complexity (c: bigO) : string =
  match c with
  | Constant -> "O(1)"
  | Logarithmic -> "O(log n)"
  | Linear -> "O(n)"
  | Linearithmic -> "O(n log n)"
  | Quadratic -> "O(n²)"
  | Cubic -> "O(n³)"
  | Exponential -> "O(2ⁿ)"
  | Factorial -> "O(n!)"

let all = [
  Constant; Logarithmic; Linear; Linearithmic;
  Quadratic; Cubic; Exponential; Factorial
]
