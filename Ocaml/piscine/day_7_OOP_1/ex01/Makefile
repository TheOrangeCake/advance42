NAME = ex01

SRCS = people.ml doctor.ml main.ml
OBJS = $(SRCS:.ml=.cmx)

all: $(NAME)

$(NAME): $(OBJS)
	@ocamlopt $(OBJS) -o $(NAME)
	@echo "Built $(NAME)"

doctor.cmx: people.cmx
main.cmx: doctor.cmx people.cmx


%.cmx: %.ml
	@ocamlopt -c $<

clean:
	@rm -f *.cmi *.cmx *.o

fclean: clean
	@rm -f $(NAME)

re: fclean all

.PHONY: all clean fclean re
