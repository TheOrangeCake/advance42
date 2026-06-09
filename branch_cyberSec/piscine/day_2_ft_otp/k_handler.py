from logger import info

def handle(key: str) -> None:
    info("Got in k handler")
    # read ft_otp.key args[1]
    # (maybe) check db to see if password is expired (time step 30s)
    # use the seed and current time as moving factor to generate HOTH
    # format to 6 digits and print out the password
    # (maybe) save password to db to validate later