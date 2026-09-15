(* ************************************************************************** *)
(*                                                                            *)
(*                                                        :::      ::::::::   *)
(*   set.ml                                             :+:      :+:    :+:   *)
(*                                                    +:+ +:+         +:+     *)
(*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        *)
(*                                                +#+#+#+#+#+   +#+           *)
(*   Created: 2026/09/13 18:01:07 by hoannguy          #+#    #+#             *)
(*   Updated: 2026/09/14 23:28:19 by hoannguy         ###   ########.fr       *)
(*                                                                            *)
(* ************************************************************************** *)

module type S = sig
  type 'a t
  val return : 'a -> 'a t
  val bind : 'a t -> ('a -> 'b t) -> 'b t
  val union : 'a t -> 'a t -> 'a t
  val inter : 'a t -> 'a t -> 'a t
  val diff : 'a t -> 'a t -> 'a t
  val filter : 'a t -> ('a -> bool) -> 'a t
  val foreach : 'a t -> ('a -> unit) -> unit
  val for_all : 'a t -> ('a -> bool) -> bool
  val exists : 'a t -> ('a -> bool) -> bool

  val zero : 'a t
  val of_list : 'a list -> 'a t
end

include (struct
  type 'a t = 'a list

  let return (x: 'a) : 'a t = [x]

  let bind (set: 'a t) (f: ('a -> 'b t)) : 'b t =
    List.concat_map f set |> List.sort_uniq compare

  let union (set1: 'a t) (set2: 'a t) : 'a t =
    set1 @ set2 |> List.sort_uniq compare

  let inter (set1: 'a t) (set2: 'a t) : 'a t =
    List.filter (fun x -> List.mem x set2) set1

  let diff (set1: 'a t) (set2: 'a t) : 'a t =
    List.filter (fun x -> not (List.mem x set2)) set1

  let filter (set: 'a t) (f: ('a -> bool)) : 'a t =
    List.filter f set

  let foreach (set: 'a t) (f: ('a -> unit)) : unit =
    List.iter f set

  let for_all (set: 'a t) (f: ('a -> bool)) : bool =
    List.for_all f set

  let exists (set: 'a t) (f: ('a -> bool)) : bool =
    List.exists f set

  let zero : 'a t = []

  let of_list (lst: 'a list) : 'a t =
    lst |> List.sort_uniq compare

end : S)
