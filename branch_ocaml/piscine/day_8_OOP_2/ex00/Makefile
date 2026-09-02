NAME = ex00

SRCS = atom.ml \
	carbon.ml \
	helium.ml \
	hydrogen.ml \
	magnesium.ml \
	oxygen.ml \
	titanium.ml \
	main.ml
OBJS = $(SRCS:.ml=.cmx)

all: $(NAME)

$(NAME): $(OBJS)
	@ocamlopt $(OBJS) -o $(NAME)
	@echo "Built $(NAME)"

carbon.cmx: atom.cmx
helium.cmx: atom.cmx
hydrogen.cmx: atom.cmx
magnesium.cmx: atom.cmx
oxygen.cmx: atom.cmx
titanium.cmx: atom.cmx
main.cmx: carbon.cmx \
	helium.cmx \
	hydrogen.cmx \
	magnesium.cmx \
	oxygen.cmx \
	titanium.cmx \
	atom.cmx

%.cmx: %.ml
	@ocamlopt -c $<

clean:
	@rm -f *.cmi *.cmx *.o

fclean: clean
	@rm -f $(NAME)

re: fclean all

.PHONY: all clean fclean re
