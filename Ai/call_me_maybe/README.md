*This project has been created as part of the 42 curriculum by hoannguy.*

# Call me maybe

</br>
</br>

TODO:
- README
- Token visualization
- makefile debug rule

## Description
Bridge the gap between LLM non deterministic output and function deterministic parameters by guarantee the LLM output to be a valid JSON. The function called must be correct and the parameters are correctly formatted.
### Brief
LLM output is non deterministic by default and depend on various factors like training, weights, skills, etc. Programs, however, are often rely on preformatted output. This create a friction between LLM and programs.
### Goal
The goal of the project is to close the gap between LLM and programs by making the LLM output deterministic. The techniques used are **Function Calling** and **Constrained Decoding**.

</br>
</br>

## Instructions
### Install UV
```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```
### Create environnement
```bash
make install
```
### Run program
- No argument
```bash
make run
```
- With arguments
```bash
make ARGS= \
"--input "path"" \
"--functions_definition "path"" \
"--output "path""
```

</br>
</br>

## Resources
### Documents
- [Function Calling](https://www.geeksforgeeks.org/artificial-intelligence/function-calling-in-llms/)
- [Constrained Decoding](https://docs.nvidia.com/deeplearning/triton-inference-server/user-guide/docs/tutorials/Feature_Guide/Constrained_Decoding/README.html)
- [Docstring google style](https://sphinxcontrib-napoleon.readthedocs.io/en/latest/example_google.html)
- [Qwen Prompt Engineering](https://qwen-ai.com/prompts/)
- [Pydantic Validation](https://pydantic.dev/docs/validation/latest/get-started/)
### AI use
AI was used to explore new concepts, validate approaches, find edge cases and some light code formatting.

</br>
</br>

## Algorithm explanation


## Concepts
### Function Calling
Function calling or Tool calling is a method to improve LLMs response quality. Programmers will provide LLMs with tools and APIs that LLMs can use. LLMs will parse the user input, decide what tools and APIs to use, then parse user input to tools / APIs arguments and call the tools. LLMs will then incorporate the result into its response.

### Constrained decoding
Constrained decoding is an act of restricting the next token choice in every step of generation. This ensure the final output match a specific structure or schema.

</br>
</br>

## Tools
| Tool | Purpose |
|:-----|:--------|
|uv|package and venv manager|
|flake8|python linter|
|mypy|static type checking|
|pydantic| data validation. JSON parsing and serialize|

</br>
</br>



