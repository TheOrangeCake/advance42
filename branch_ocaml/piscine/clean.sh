#!/bin/bash

# Remove OCaml build artifacts from every subdirectory.

cd "$(dirname "$0")" || exit 1

find . -type f \( -name 'a.out' -o -name '*.cmi' -o -name '*.cmx' -o -name '*.o' \) -print -delete
