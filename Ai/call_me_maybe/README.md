# Call me maybe

## Concepts
### Function Calling
Function calling or Tool calling is a method to improve LLMs response quality. Programmers will provide LLMs with tools and APIs that LLMs can use. LLMs will parse the user input, decide what tools and APIs to use, then parse user input to tools / APIs arguments and call the tools. LLMs will then incorporate the result into its response.
Source:
- [geeksforgeeks](https://www.geeksforgeeks.org/artificial-intelligence/function-calling-in-llms/)
- [NVIDA](https://docs.nvidia.com/deeplearning/triton-inference-server/user-guide/docs/tutorials/Feature_Guide/Function_Calling/README.html)

### Constrained decoding
After function calling, the tool/API response will be feed back to LLMs along with a formatting tool/API and a crafted prompt to control the output formatting. This ensure the final result formatting, very useful when the result is needed to be structurely correct to be then processed later on by another system.
Source:
- [NVIDA](https://docs.nvidia.com/deeplearning/triton-inference-server/user-guide/docs/tutorials/Feature_Guide/Constrained_Decoding/README.html)

</br>
</br>

## Tools
| Tool | Purpose |
|:-----|:--------|
|uv|package and venv manager|
|flake8|python linter|
|mypy|static type checking|
|pydantic| data validation. JSON parsing and serialize|
|json|JSON manipulation|
|numpy|Better array object|

</br>
</br>

## Steps

</br>
</br>

## Install UV
```bash
curl -LsSf https://astral.sh/uv/install.sh | sh
```

