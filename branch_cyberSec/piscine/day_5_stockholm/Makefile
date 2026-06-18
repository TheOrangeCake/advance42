.DEFAULT_GOAL := all

NAME = stockholm
DIR = src/
SODIUM = libsodium-stable
SOURCE = $(DIR)main.c $(DIR)infect.c $(DIR)desinfect.c

OBJ_DIR = obj
OBJECTS = $(patsubst $(DIR)%.c,$(OBJ_DIR)/%.o,$(SOURCE))

GCC = gcc
FLAGS = -Wextra -Wall -Werror -MMD -MP -I/usr/local/include
LDFLAGS = -L/usr/local/lib -lsodium
RM = rm -f

all: $(NAME)

$(OBJ_DIR)/%.o: $(DIR)%.c
	@mkdir -p $(OBJ_DIR)
	@$(GCC) $(FLAGS) -c $< -o $@

$(NAME): $(OBJECTS) | $(OBJ_DIR)
	@$(GCC) $(FLAGS) $(OBJECTS) -o $(NAME) $(LDFLAGS)
	@echo "Built $(NAME)"

install:
	tar -xvf libsodium.gz
	cd $(SODIUM) && \
	./configure && \
	make && make check && \
	sudo make install && \
	cd .. && \
	make

clean:
	@$(RM) -r $(OBJ_DIR)
	@$(RM) -r $(SODIUM)
	@echo "Deleted object files"

fclean: clean
	@$(RM) $(NAME)
	@echo "Deleted $(NAME)"

re: fclean all

.PHONY: all clean fclean re install

-include $(OBJECTS:.o=.d)