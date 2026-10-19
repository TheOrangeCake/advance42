# Call me maybe

</br>
</br>


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

## Install UV
```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```

