# Call me maybe

## Steps
- Create a function - Picker() - to pick the next token or force the output structure.
	- The params should be the logits, the current output in token id list, the function list and the vocab list
	- We should keep track of the current state: NAME, PARAMS
		- if state is NAME, we check the token + output in the function list, we can either auto complete it if only 1 match, if 0 match then we can considered the prompt failed. TBD if we continue with the rest or just have a predefined error json.
		- if state is PARAMS, then we know the function used and its params type. Based on the type, we can force close the output and move onto the next. This state will need more work and TBD once we got here.

- Craft prompt using prompt engineering. This prompt will be composed of the instruction prompt, the function definition list and the expected output format. Then encode() the prompt.

- Create an outputs list
- Loop through input
	- Create an encoded output which is empty at the start
	- Loop till break
		- Call Small_LLM_Model get_logits_from_input_ids() with the prompt + input + output-> get back the token list -> match with vocab to get the token id
		- pass the token tensor to the Picker() -> which return the encoded prompt for next loop, or break the loop.
	- Decode the output and append to the outputs list
- Validate the outputs list with Pydantic in FileHandler
- Write the validated outputs list to output file


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

