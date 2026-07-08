NAME = inquisitor

GCC = g++
FLAGS = -Wall -Wextra -Werror -std=c++17

PCPP_VERSION = v25.05
HEADER  = -I header $(shell pkg-config --cflags PcapPlusPlus 2>/dev/null)
LDFLAGS = $(shell pkg-config --libs PcapPlusPlus 2>/dev/null)

SRCS = $(wildcard src/*.cpp)
OBJS = $(SRCS:.cpp=.o)

all: $(NAME)

$(NAME): $(OBJS)
	@$(GCC) $(FLAGS) -o $@ $^ $(LDFLAGS)
	@echo "Compiled binary $(NAME)"

%.o: %.cpp
	@$(GCC) $(FLAGS) $(HEADER) -c $< -o $@

install:
	sudo apt-get install -y libpcap-dev cmake git
	rm -rf /tmp/pcpp-src
	git clone --depth 1 --branch $(PCPP_VERSION) https://github.com/seladb/PcapPlusPlus.git /tmp/pcpp-src
	cmake -S /tmp/pcpp-src -B /tmp/pcpp-src/build
	cmake --build /tmp/pcpp-src/build -j
	sudo cmake --install /tmp/pcpp-src/build
	sudo ldconfig
	rm -rf /tmp/pcpp-src

clean:
	@rm -f $(OBJS)
	@echo "Deleted object files"

fclean: clean
	@rm -f $(NAME)
	@echo "Deleted binary $(NAME)"

re: fclean all

.PHONY: all install clean fclean re
