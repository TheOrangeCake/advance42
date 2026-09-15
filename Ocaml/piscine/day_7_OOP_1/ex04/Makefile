NAME = ex04

SRCS = people.ml doctor.ml dalek.ml army.ml galifrey.ml main.ml
OBJS = $(SRCS:.ml=.cmx)

all: $(NAME)

$(NAME): $(OBJS)
	@ocamlopt $(OBJS) -o $(NAME)
	@echo "Built $(NAME)"

army.cmx: dalek.cmx doctor.cmx people.cmx
dalek.cmx: people.cmx
doctor.cmx: people.cmx
galifrey.cmx: dalek.cmx doctor.cmx people.cmx
main.cmx: galifrey.cmx army.cmx doctor.cmx dalek.cmx people.cmx


%.cmx: %.ml
	@ocamlopt -c $<

clean:
	@rm -f *.cmi *.cmx *.o

fclean: clean
	@rm -f $(NAME)

re: fclean all

.PHONY: all clean fclean re
