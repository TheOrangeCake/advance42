(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/02 16:41:42 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/03 00:48:37 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

class test_water =
  object (self)
  inherit Molecule.molecule "Water (test)" 
  [
    (new Oxygen.oxygen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
    (new Hydrogen.hydrogen :> Atom.atom);
  ]
end

let () =
  let molecules = [
    (new Trinitrotoluene.trinitrotoluene :> Molecule.molecule);
    (new Water.water :> Molecule.molecule);
    (new Carbon_dioxide.carbon_dioxide :> Molecule.molecule);
    (new Methane.methane :> Molecule.molecule);
    (new Ammonia.ammonia :> Molecule.molecule);
  ] in
  List.iter (fun x -> print_endline x#to_string) molecules;
  print_newline ();

  let w1 = new Water.water in
  Printf.printf "water equals water -> %b\n"
    (w1#equals (new Water.water :> Molecule.molecule));
    
  Printf.printf "water equals test water -> %b\n"
    (w1#equals (new test_water :> Molecule.molecule));
    
  Printf.printf "water equals carbon dioxide -> %b\n"
    (w1#equals (new Carbon_dioxide.carbon_dioxide :> Molecule.molecule));
    
  Printf.printf "methane equals ammonia -> %b\n"
    ((new Methane.methane)#equals (new Ammonia.ammonia :> Molecule.molecule))
