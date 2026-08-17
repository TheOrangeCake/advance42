(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/12 15:03:00 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/18 01:51:41 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let rec print_deck deck =
  match deck with
  | [] -> ()
  | h :: t ->
    print_string h;
    print_char ' ';
    print_deck t

let main () =
  let deck1 = Deck.newDeck () in
  let deck2 = Deck.newDeck () in
  print_endline "DECK 1: ";
  print_deck (Deck.toStringList deck1);
  print_char '\n';
  print_deck (Deck.toStringListVerbose deck1);
  print_char '\n';
  print_endline "----------";
  print_endline "DECK 2: ";
  print_deck (Deck.toStringList deck2);
  print_char '\n';
  print_deck (Deck.toStringListVerbose deck2);
  print_char '\n';
  print_endline "----------";
  print_endline "DRAW FROM DECK 2";
  let draw = Deck.drawCard deck2 in
  print_endline (draw |> fst |> Deck.Card.toStringVerbose);
  print_endline "DRAW ANOTHER FROM DECK 2";
  let draw2 = Deck.drawCard (snd draw) in
  print_endline (draw2 |> fst |> Deck.Card.toStringVerbose)

let () =
  main ()