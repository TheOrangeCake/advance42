(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   main.ml                                            :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/08/07 23:20:20 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/08/12 14:59:40 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

let main () =
  List.iter (
    fun x -> 
      print_string (Card.toString x);
      print_string "\t -> ";
      print_endline (Card.toStringVerbose x)) Card.all;

  let twoHeart = Card.newCard Card.Value.T2 Card.Color.Heart in
  let queenDiamond = Card.newCard Card.Value.Queen Card.Color.Diamond in
  let twoHeartDup = Card.newCard Card.Value.T2 Card.Color.Heart in
  begin
    print_string "COMPARE 2 Heart with Queen Diamond -> ";
    print_int (Card.compare twoHeart queenDiamond);
    print_char '\n'
  end;
  begin
    print_string "COMPARE Queen Diamond with 2 Heart-> ";
    print_int (Card.compare queenDiamond twoHeart);
    print_char '\n'
  end;
  begin
    print_string "COMPARE 2 Heart with 2 Heart -> ";
    print_int (Card.compare twoHeart twoHeartDup);
    print_char '\n'
  end;
  begin
    print_string "MAX of 2 Heart and Queen Diamond -> ";
    print_endline (Card.toStringVerbose (Card.max twoHeart queenDiamond))
  end;
  begin
    print_string "MIN of 2 Heart and Queen Diamond -> ";
    print_endline (Card.toStringVerbose (Card.min twoHeart queenDiamond))
  end;
  let kingClub = Card.newCard Card.Value.King Card.Color.Club in
  let quad = [queenDiamond; twoHeart; kingClub; twoHeartDup] in
  begin
    print_string "BEST of Queen Diamond, 2 Heart, King Club and 2 Heart -> ";
    print_endline (Card.toStringVerbose (Card.best quad))
  end;
  begin
    print_string "Is 2 Heart of type Heart -> ";
    print_endline (if Card.isHeart twoHeart = true then "True" else "False")
  end;
  begin
    print_string "Is Queen Diamond of type Heart -> ";
    print_endline (if Card.isHeart queenDiamond = true then "True" else "False")
  end

let () = main ()
