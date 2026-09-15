#!/bin/bash

sudo apt install opam
opam init -y
eval $(opam env --switch=default)
opam install ocaml-lsp-server
