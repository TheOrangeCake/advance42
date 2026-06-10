from logger import *

def handle(key: str) -> None:
    info("Got in k handler")
    # read ft_otp.key
    # read ft_otp.secret

    # decrypt key using secret for the seed
    # validate seed
    # transform seed string to raw bytes for HMAC

    # check current time
    # get the current slot using floor(unix_time / 30)
    # transform current slot to 8 bytes big endian
     
    # use the transformed seed and current slot to generate a hash using HMAC-SHA1
    # truncate the hash the OTP
        # offset = last byte & 0x0F
        # take 4 bytes at offset, mask top bit (& 0x7FFFFFFF)
        # % 1_000_000, then zero-pad to 6 digits
    
    # print out the OTP
    