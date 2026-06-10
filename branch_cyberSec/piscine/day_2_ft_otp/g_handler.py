from logger import *

def handle(key: str) -> None:
    info("Got in g handler")
    # check if argument is file or raw
        # if file
            # open and read data
    # trim
    # validate the key (64 chars, hex format)
    # generated a secret key to ft_otp.secret
    # encrypt the hex key with secret key
    # save encrypted hex key to ft_otp.key as seed
    # print success "Key was successfully saved in ft_otp.key."