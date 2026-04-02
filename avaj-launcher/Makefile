RED := \033[31m
GREEN := \033[32m
YELLOW := \033[33m
BLUE := \033[34m
RESET := \033[0m

SOURCE = sources.txt

build:
	@echo "$(BLUE)Getting java files...$(RESET)"
	@find * -name "*.java" > sources.txt
	@echo "$(BLUE)Compiling...$(RESET)"
	@javac @$(SOURCE)
	@echo "$(GREEN)Build done!$(RESET)"

clean:
	@rm $$(find * -name "*.class")
	@rm $(SOURCE)
	@echo "$(GREEN)Cleaning done!$(RESET)"

.PHONY: build clean