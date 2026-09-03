NAME = ex02

SRCS = atom.ml \
	carbon.ml \
	hydrogen.ml \
	molecule.ml \
	alkane.ml \
	methane.ml \
	octane.ml \
	ethane.ml \
	main.ml
OBJS = $(SRCS:.ml=.cmx)

all: $(NAME)

$(NAME): $(OBJS)
	@ocamlopt $(OBJS) -o $(NAME)
	@echo "Built $(NAME)"

carbon.cmx: atom.cmx
hydrogen.cmx: atom.cmx
molecule.cmx: atom.cmx
methane.cmx: alkane.cmx
octane.cmx: alkane.cmx
ethane.cmx: alkane.cmx
alkane.cmx: molecule.cmx \
	carbon.cmx \
	hydrogen.cmx
main.cmx: carbon.cmx \
	hydrogen.cmx \
	atom.cmx \
	molecule.cmx \
	methane.cmx \
	ethane.cmx \
	octane.cmx \
	alkane.cmx

%.cmx: %.ml
	@ocamlopt -c $<

clean:
	@rm -f *.cmi *.cmx *.o

fclean: clean
	@rm -f $(NAME)

re: fclean all

.PHONY: all clean fclean re
