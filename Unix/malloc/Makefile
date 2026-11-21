ifeq ($(HOSTTYPE),)
	HOSTTYPE := $(shell uname -m)_$(shell uname -s)
endif

LIB_NAME = libft_malloc_$(HOSTTYPE).so
SYMLINK = libft_malloc.so

FLAGS = -Wall -Wextra -Werror -fPIC -pthread

SRC_DIR = src
SRC =	$(SRC_DIR)/free.c		\
		$(SRC_DIR)/malloc.c		\
		$(SRC_DIR)/realloc.c	\
		$(SRC_DIR)/calloc.c		\
		$(SRC_DIR)/zone.c		\
		$(SRC_DIR)/block.c		\
		$(SRC_DIR)/print.c		\
		$(SRC_DIR)/utils.c		\

OBJECTS = $(SRC:.c=.o)

all: $(LIB_NAME)

.c.o:
	@gcc $(FLAGS) -c $< -o $(<:.c=.o)

$(OBJECTS): $(SRC_DIR)/libft_malloc.h

$(LIB_NAME): $(OBJECTS)
	@echo "Building .so lib..."
	@gcc -shared $(FLAGS) -o $(LIB_NAME) $(OBJECTS)
	@echo "-> Built $(LIB_NAME) !"
	@echo "Creating symlink..."
	@rm -f $(SYMLINK)
	@ln -s $(LIB_NAME) $(SYMLINK)
	@echo "-> Symlink $(SYMLINK) created !"

test: $(LIB_NAME)
	@gcc -Wno-free-nonheap-object -Wno-alloc-size-larger-than test/main.c -I src -L. -lft_malloc -Wl,-rpath,"$(CURDIR)" -o test_malloc
	@./test_malloc

clean:
	@echo "Removing .o files..."
	@rm -f $(OBJECTS)
	@echo "-> All .o files removed !"

fclean:	clean
	@echo "Removing lib files..."
	@rm -f $(LIB_NAME) $(SYMLINK)
	@echo "-> Lib files $(LIB_NAME) and $(SYMLINK) removed !"
	@echo "Removing test binary..."
	@rm -f test_malloc
	@echo "-> Test binary removed !"

re: fclean all

.PHONY: all test clean fclean re
