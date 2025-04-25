import os

def generate_random_bytes(filename, size):
    with open(filename, 'wb') as f:
        f.write(os.urandom(size))

filename = 'random_bytes.bin'
size = 3289
generate_random_bytes(filename, size)
