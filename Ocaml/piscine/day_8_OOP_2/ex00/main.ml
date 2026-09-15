(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/02 16:41:42 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/02 22:27:22 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let () =
  let atoms : Atom.atom list = [
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Helium.helium :> Atom.atom);
    (new Carbon.carbon :> Atom.atom);
    (new Oxygen.oxygen :> Atom.atom);
    (new Magnesium.magnesium :> Atom.atom);
    (new Titanium.titanium :> Atom.atom);
  ] in
  List.iter (fun a -> print_endline a#to_string) atoms;
  print_newline ();

  let c1 = new Carbon.carbon and c2 = new Carbon.carbon in
  Printf.printf "carbon equal carbon : %b\n" (c1#equals (c2 :> Atom.atom));
  Printf.printf "carbon equal oxygen : %b\n" (c1#equals (new Oxygen.oxygen :> Atom.atom))
