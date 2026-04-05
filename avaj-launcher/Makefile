RED := \033[31m
GREEN := \033[32m
YELLOW := \033[33m
BLUE := \033[34m
RESET := \033[0m

SOURCE = sources.txt
NAME = Simulator
CLASS_FOLDER = classes
SCENARIO=scenario.txt

build:
	@echo "$(BLUE)Getting java files...$(RESET)"
	@find * -name "*.java" > $(SOURCE)
	@echo "$(BLUE)Compiling...$(RESET)"
	@javac -d $(CLASS_FOLDER) @$(SOURCE)
	@echo "$(GREEN)Build done!$(RESET)"

run:
	@echo "$(BLUE)Running $(NAME) with $(SCENARIO)$(RESET)"
	@java -cp $(CLASS_FOLDER) $(NAME) $(SCENARIO)

clean:
	@rm -rf $(CLASS_FOLDER)
	@rm $(SOURCE)
	@echo "$(GREEN)Cleaning done!$(RESET)"

.PHONY: run build clean