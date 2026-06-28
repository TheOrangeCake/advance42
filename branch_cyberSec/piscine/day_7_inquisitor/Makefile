NAME = inquisitor

GCC = g++
FLAGS = -Wall -Wextra -Werror -std=c++17
HEADER = -I header
LDFLAGS = -lpcap

SRCS = $(wildcard src/*.cpp)
OBJS = $(SRCS:.cpp=.o)

all: $(NAME)

$(NAME): $(OBJS)
	$(GCC) $(FLAGS) $(HEADER) -o $@ $^ $(LDFLAGS)

%.o: %.cpp
	$(GCC) $(FLAGS) -c $< -o $@

install:
	sudo apt-get install -y libpcap-dev

clean:
	@rm -f $(OBJS)
	@echo "Deleted object files"

fclean: clean
	@rm -f $(NAME)
	@echo "Deleted binary $(NAME)"

re: fclean all

.PHONY: all install clean fclean re
