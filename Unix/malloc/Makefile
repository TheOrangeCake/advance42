ifeq ($(HOSTTYPE),)
	HOSTTYPE := $(shell uname -m)_$(shell uname -s)
endif

LIB_NAME = libft_malloc_$(HOSTTYPE).so
SYMLINK = libft_malloc.so
FLAGS = -Wall -Wextra -Werror -fPIC

SRC_DIR = src
SRC =	$(SRC_DIR)/free.c		\
		$(SRC_DIR)/malloc.c	\
		$(SRC_DIR)/realloc.c	\

OBJECTS = ${SRC:.c=.o}

all: run

.c.o:
	@gcc ${FLAGS} -c -I. $< -o ${<:.c=.o}

run: $(OBJECTS)
	@echo "Building $(LIB_NAME)..."
	@gcc -shared -o $(LIB_NAME) $(OBJECTS)
	@echo "Built $(LIB_NAME) !"

clean:
	@echo "Removing .o files..."
	@rm -f ${OBJECTS}
	@echo "All .o files removed !"

fclean:		clean
	@echo "Removing lib file $(LIB_NAME)..."
	@rm -f ${LIB_NAME}
	@echo "Lib file $(LIB_NAME) removed !"

re: fclean all

.PHONY: all run clean fclean re
