from logger import *

def handle(key: str) -> None:
    info("Got in g handler")
    # parse hex key (raw or file)
    # encrypt and save hex key to ft_otp.key to use as seed
