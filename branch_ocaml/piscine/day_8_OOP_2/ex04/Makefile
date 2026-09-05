NAME = ex04

SRCS = atom.ml \
	carbon.ml \
	hydrogen.ml \
	oxygen.ml \
	molecule.ml \
	alkane.ml \
	methane.ml \
	ethane.ml \
	octane.ml \
	water.ml \
	carbon_dioxide.ml \
	dioxygen.ml \
	reaction.ml \
	alkane_combustion.ml \
	main.ml
OBJS = $(SRCS:.ml=.cmx)

all: $(NAME)

$(NAME): $(OBJS)
	@ocamlopt $(OBJS) -o $(NAME)
	@echo "Built $(NAME)"

carbon.cmx: atom.cmx
hydrogen.cmx: atom.cmx
oxygen.cmx: atom.cmx
molecule.cmx: atom.cmx
water.cmx: molecule.cmx \
	hydrogen.cmx \
	oxygen.cmx
carbon_dioxide.cmx: molecule.cmx \
	carbon.cmx \
	oxygen.cmx
dioxygen.cmx: molecule.cmx \
	oxygen.cmx
alkane.cmx: molecule.cmx \
	carbon.cmx \
	hydrogen.cmx
methane.cmx: alkane.cmx
ethane.cmx: alkane.cmx
octane.cmx: alkane.cmx
reaction.cmx: molecule.cmx
alkane_combustion: reaction.cmx
main.cmx: atom.cmx \
	carbon.cmx \
	hydrogen.cmx \
	oxygen.cmx \
	molecule.cmx \
	alkane.cmx \
	methane.cmx \
	ethane.cmx \
	octane.cmx \
	water.cmx \
	carbon_dioxide.cmx \
	dioxygen.cmx \
	reaction.cmx \
	alkane_combustion.cmx

%.cmx: %.ml
	@ocamlopt -c $<

clean:
	@rm -f *.cmi *.cmx *.o

fclean: clean
	@rm -f $(NAME)

re: fclean all

.PHONY: all clean fclean re
