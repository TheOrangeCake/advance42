ifeq ($(HOSTTYPE),)
	HOSTTYPE := $(shell uname -m)_$(shell uname -s)
endif

LIB_NAME = libft_malloc_$(HOSTTYPE).so
SYMLINK = libft_malloc.so

FLAGS = -Wall -Wextra -Werror -fPIC

SRC_DIR = src
SRC =	$(SRC_DIR)/free.c		\
		$(SRC_DIR)/malloc.c		\
		$(SRC_DIR)/realloc.c	\
		$(SRC_DIR)/utils.c		\

OBJECTS = $(SRC:.c=.o)

all: $(LIB_NAME)

.c.o:
	@gcc $(FLAGS) -c $< -o $(<:.c=.o)

$(LIB_NAME): $(OBJECTS)
	@echo "Building .so lib..."
	@gcc -shared -o $(LIB_NAME) $(OBJECTS)
	@echo "-> Built $(LIB_NAME) !"
	@echo "Creating symlink..."
	@rm -f $(SYMLINK)
	@ln -s $(LIB_NAME) $(SYMLINK)
	@echo "-> Symlink $(SYMLINK) created !"

clean:
	@echo "Removing .o files..."
	@rm -f $(OBJECTS)
	@echo "-> All .o files removed !"

fclean:		clean
	@echo "Removing lib files..."
	@rm -f $(LIB_NAME) $(SYMLINK)
	@echo "-> Lib files $(LIB_NAME) and $(SYMLINK) removed !"

re: fclean all

.PHONY: all clean fclean re
