BOLD	:= \033[1m
GRAY	:= \033[90m
GREEN	:= \033[32m
BLUE	:= \033[34m
RESET	:= \033[0m
ERASE	:= \r\033[2K

ENCODER_NAME	= machine_encoder
BUILDER_NAME	= utm_builder

SRCS_DIR		= src
COMMON_DIR		= $(SRCS_DIR)/common
ENCODER_DIR		= $(SRCS_DIR)/encoder
BUILDER_DIR		= $(SRCS_DIR)/builder

ENCODER_LIBS	?= yojson core
BUILDER_LIBS	?= yojson core

APPS			= encoder builder
LIBS			= $(sort $(ENCODER_LIBS) $(BUILDER_LIBS))

BUILD_DIR		= build
OPT_DIR			= opt
BYT_DIR			= byt

OPAM_ENV		= eval $$(opam env) >/dev/null 2>&1;
OPAMROOT		:= $(HOME)/.opam
OPAM_INSTALL_URL = https://opam.ocaml.org/install.sh

.DEFAULT_GOAL := all

# ===========================================================================
# Top level: make sure opam and the libraries are there, then build every
# application in a sub-make. The build rules are only parsed by the sub-make
# (APP is set), so ocamldep never runs before the toolchain is guaranteed.
# ===========================================================================
ifndef APP

all: setup
	@for app in $(APPS); do \
		$(MAKE) --no-print-directory APP=$$app build || exit 1; \
	done

encoder builder: setup
	@$(MAKE) --no-print-directory APP=$@ build

opt byt: setup
	@for app in $(APPS); do \
		$(MAKE) --no-print-directory APP=$$app $@ || exit 1; \
	done

setup: check-opam check-libs

check-opam:
	@if ! command -v opam >/dev/null 2>&1; then \
		printf "$(BOLD)$(BLUE)opam not found, installation...$(RESET)\n"; \
		bash -c "sh <(curl -fsSL $(OPAM_INSTALL_URL))" --no-backup -y || \
			(printf "$(GRAY)Automatic opam installation failed. Install it manually : https://opam.ocaml.org/doc/Install.html$(RESET)\n" && exit 1); \
		printf "$(GREEN)  ✓ opam installed$(RESET)\n"; \
	fi
	@if [ ! -d "$(OPAMROOT)" ]; then \
		printf "$(BOLD)$(BLUE)Opam initialization...$(RESET)\n"; \
		opam init -y --disable-sandboxing >/dev/null; \
		printf "$(GREEN)  ✓ opam initialized$(RESET)\n"; \
	fi

check-libs: check-opam
	@eval $$(opam env) >/dev/null 2>&1; \
	for lib in $(LIBS); do \
		if ! ocamlfind list 2>/dev/null | awk '{print $$1}' | grep -qx "$$lib"; then \
			printf "$(GRAY)  Missing libraries : $$lib, installation via opam...$(RESET)\n"; \
			opam install -y "$$lib" || \
				(printf "$(GRAY)Installation failed: $$lib$(RESET)\n" && exit 1); \
			printf "$(GREEN)  ✓ $$lib installed$(RESET)\n"; \
		fi; \
	done

clean:
	@printf "$(BOLD)$(BLUE)Cleaning objects...$(RESET)\n"
	@printf "$(GRAY)  Removing $(BUILD_DIR)...$(RESET)" && \
	 rm -rf $(BUILD_DIR) && \
	 printf "$(ERASE)"
	@printf "$(GREEN)  ✓ Build files cleaned$(RESET)\n"

fclean: clean
	@for name in $(ENCODER_NAME) $(BUILDER_NAME); do \
		for f in $$name $$name.opt $$name.byt; do \
			printf "$(GRAY)  Removing $$f...$(RESET)" && \
			rm -f $$f && \
			printf "$(ERASE)$(GREEN)  ✓ $$f cleaned$(RESET)\n"; \
		done; \
	done

re: fclean
	@$(MAKE) --no-print-directory all

.PHONY: all encoder builder opt byt setup check-opam check-libs clean fclean re

# ===========================================================================
# Application level: one sub-make per application (APP=encoder|builder)
# ===========================================================================
else

ifeq ($(APP),encoder)
	NAME		= $(ENCODER_NAME)
	APP_DIR		= $(ENCODER_DIR)
	APP_LIBS	= $(ENCODER_LIBS)
else ifeq ($(APP),builder)
	NAME		= $(BUILDER_NAME)
	APP_DIR		= $(BUILDER_DIR)
	APP_LIBS	= $(BUILDER_LIBS)
else
	$(error Unknown APP '$(APP)' (expected: $(APPS)))
endif

CAMLOPT		= $(OPAM_ENV) ocamlfind ocamlopt
CAMLC		= $(OPAM_ENV) ocamlfind ocamlc
CAMLDEP		= $(OPAM_ENV) ocamldep

SRC_DIRS	= $(wildcard $(COMMON_DIR)) $(APP_DIR)
SRCS		= $(sort $(foreach d,$(SRC_DIRS),$(wildcard $(d)/*.ml)))
MLI_SRCS	= $(sort $(foreach d,$(SRC_DIRS),$(wildcard $(d)/*.mli)))
DEP_INCLUDES = $(addprefix -I ,$(SRC_DIRS))

ifeq ($(strip $(SRCS)),)
	$(error No .ml file found in: $(SRC_DIRS))
endif

APP_BUILD	= $(BUILD_DIR)/$(APP)
OPT_BUILD	= $(APP_BUILD)/$(OPT_DIR)
BYT_BUILD	= $(APP_BUILD)/$(BYT_DIR)
DEP_FILE_OPT = $(OPT_BUILD)/.depend
DEP_FILE_BYT = $(BYT_BUILD)/.depend

empty :=
space := $(empty) $(empty)
comma := ,
PACKAGES	:= $(subst $(space),$(comma),$(strip $(APP_LIBS)))
PKG_FLAGS	:= $(if $(strip $(APP_LIBS)),-package $(PACKAGES))
LINK_FLAGS	:= $(if $(strip $(APP_LIBS)),-package $(PACKAGES) -linkpkg)

FLAGS_OPT	= -I $(OPT_BUILD)
FLAGS_BYT	= -I $(BYT_BUILD)

define GEN_DEPEND
	@mkdir -p $(@D)
	@$(CAMLDEP) -sort $(DEP_INCLUDES) $(SRCS) $(MLI_SRCS) > $@.sorted && \
	 $(CAMLDEP) $(DEP_INCLUDES) $(SRCS) $(MLI_SRCS) > $@.raw && \
	 { printf 'SORTED_SRCS_$(1) := %s\n' "$$(cat $@.sorted)"; \
	   sed 's|[^ ]*/\([^ /]*\.cm[iox]\)|$(2)/\1|g' $@.raw; } > $@ && \
	 rm -f $@.sorted $@.raw
endef

$(DEP_FILE_OPT): $(SRCS) $(MLI_SRCS)
	$(call GEN_DEPEND,OPT,$(OPT_BUILD))

$(DEP_FILE_BYT): $(SRCS) $(MLI_SRCS)
	$(call GEN_DEPEND,BYT,$(BYT_BUILD))

include $(DEP_FILE_OPT)
include $(DEP_FILE_BYT)

OPTOBJS = $(patsubst %.ml,$(OPT_BUILD)/%.cmx,$(notdir $(filter %.ml,$(SORTED_SRCS_OPT))))
BYTOBJS = $(patsubst %.ml,$(BYT_BUILD)/%.cmo,$(notdir $(filter %.ml,$(SORTED_SRCS_BYT))))

vpath %.ml $(SRC_DIRS)
vpath %.mli $(SRC_DIRS)

build: opt byt $(NAME)
	@:

opt: $(NAME).opt
	@:

byt: $(NAME).byt
	@:

$(NAME):
	@[ -L $(NAME) ] || \
	 (printf "\n$(BOLD)$(BLUE)Creating the symbolic link$(RESET)\n" && \
	  printf "$(GRAY)  Creating symlink $(NAME) -> $(NAME).opt...$(RESET)" && \
	  ln -s $(NAME).opt $(NAME) && \
	  printf "$(ERASE)$(GREEN)  ✓ $(NAME) -> $(NAME).opt$(RESET)\n")

$(APP_BUILD)/.opt_start: $(SRCS) $(MLI_SRCS)
	@mkdir -p $(@D)
	@printf "$(BOLD)$(BLUE)Compiling $(NAME).opt$(RESET)\n"
	@touch $@

$(APP_BUILD)/.byt_start: $(SRCS) $(MLI_SRCS)
	@mkdir -p $(@D)
	@printf "\n$(BOLD)$(BLUE)Compiling $(NAME).byt$(RESET)\n"
	@touch $@

$(NAME).opt: $(OPTOBJS)
	@printf "$(BOLD)Linking $(NAME).opt$(RESET)\n"
	@$(CAMLOPT) $(FLAGS_OPT) $(LINK_FLAGS) -o $@ $^
	@printf "$(GREEN)  ✓ $(NAME).opt ready$(RESET)\n"

$(OPT_BUILD)/%.cmi: %.mli | $(APP_BUILD)/.opt_start
	@mkdir -p $(@D)
	@printf "$(GRAY)  $<...$(RESET)" && \
	 $(CAMLOPT) $(FLAGS_OPT) $(PKG_FLAGS) -c -o $@ $< && \
	 printf "$(ERASE)$(GREEN)  ✓ $<$(RESET)\n"

$(OPT_BUILD)/%.cmx: %.ml | $(APP_BUILD)/.opt_start
	@mkdir -p $(@D)
	@printf "$(GRAY)  $<...$(RESET)" && \
	 $(CAMLOPT) $(FLAGS_OPT) $(PKG_FLAGS) -c -o $@ $< && \
	 printf "$(ERASE)$(GREEN)  ✓ $<$(RESET)\n"

$(NAME).byt: $(BYTOBJS)
	@printf "$(BOLD)Linking $(NAME).byt$(RESET)\n"
	@$(CAMLC) $(FLAGS_BYT) $(LINK_FLAGS) -o $@ $^
	@printf "$(GREEN)  ✓ $(NAME).byt ready$(RESET)\n"

$(BYT_BUILD)/%.cmi: %.mli | $(APP_BUILD)/.byt_start
	@mkdir -p $(@D)
	@printf "$(GRAY)  $<...$(RESET)" && \
	 $(CAMLC) $(FLAGS_BYT) $(PKG_FLAGS) -c -o $@ $< && \
	 printf "$(ERASE)$(GREEN)  ✓ $<$(RESET)\n"

$(BYT_BUILD)/%.cmo: %.ml | $(APP_BUILD)/.byt_start
	@mkdir -p $(@D)
	@printf "$(GRAY)  $<...$(RESET)" && \
	 $(CAMLC) $(FLAGS_BYT) $(PKG_FLAGS) -c -o $@ $< && \
	 printf "$(ERASE)$(GREEN)  ✓ $<$(RESET)\n"

.PHONY: build opt byt

endif