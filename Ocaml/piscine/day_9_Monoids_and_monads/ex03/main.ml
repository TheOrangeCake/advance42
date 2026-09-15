(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/13 16:15:40 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/13 17:40:50 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let print_try (t: 'a Try.t) (f: 'a -> string) =
  match t with
  | Try.Success x -> Printf.printf "Success: %s\n" (f x)
  | Try.Failure e -> Printf.printf "Failure: %s\n" ((Printexc.to_string e))

let () =
  print_endline "--- return ---";
  print_try (Try.return "Testing return") (fun str -> str);
  
  print_endline "\n--- bind ---";
  let ok_bind = Try.bind (Try.Success 10) (fun x -> Try.Success (x + 32)) in
  print_try ok_bind string_of_int;
  
  let raise_bind = Try.bind (Try.Success 10) (fun _ -> raise Not_found) in
  print_try raise_bind (fun _ -> "");

  let ko_bind = Try.bind (Try.Failure Division_by_zero) (fun _ -> raise Not_found) in
  print_try ko_bind (fun _ -> "");

  print_endline "\n--- recover ---";
  let handler e = match e with
        | Not_found -> Try.Success 404
        | Division_by_zero -> Try.Success 0
        | _ -> Try.Success 4242
  in
  let recover_failure = Try.recover raise_bind handler in
  print_try recover_failure string_of_int;
  let recover_succeed = Try.recover ok_bind handler in
  print_try recover_succeed string_of_int;

  print_endline "\n--- filter ---";
  let filter_ok = Try.filter ok_bind (fun _ -> true) in
  print_try filter_ok string_of_int;
  
  let filter_ko = Try.filter ok_bind (fun _ -> false) in
  print_try filter_ko string_of_int;

  let filter_failure = Try.filter ko_bind (fun _ -> true) in
  print_try filter_failure string_of_int;

  print_endline "\n--- flatten ---";
  let nested_ok = Try.Success (Try.Success 42) in
  print_try (Try.flatten nested_ok) string_of_int;

  let nested_ko = Try.Success (Try.Failure Not_found) in
  print_try (Try.flatten nested_ko) string_of_int;

  let outer_ko = Try.Failure Division_by_zero in
  print_try (Try.flatten outer_ko) string_of_int;

  let double_wrap = Try.bind ok_bind (fun x -> Try.return (Try.return x)) in
  print_try (Try.flatten double_wrap) string_of_int
