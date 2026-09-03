NAME = ex03

SRCS = atom.ml \
	carbon.ml \
	hydrogen.ml \
	molecule.ml \
	reaction.ml \
	main.ml
OBJS = $(SRCS:.ml=.cmx)

all: $(NAME)

$(NAME): $(OBJS)
	@ocamlopt $(OBJS) -o $(NAME)
	@echo "Built $(NAME)"

carbon.cmx: atom.cmx
hydrogen.cmx: atom.cmx
molecule.cmx: atom.cmx
reaction.cmx: molecule.cmx
main.cmx: carbon.cmx \
	hydrogen.cmx \
	atom.cmx \
	molecule.cmx \
	reaction.cmx

%.cmx: %.ml
	@ocamlopt -c $<

clean:
	@rm -f *.cmi *.cmx *.o

fclean: clean
	@rm -f $(NAME)

re: fclean all

.PHONY: all clean fclean re
