import os

def generate_random_bytes(output_filename, size):
    with open(output_filename, 'wb') as f:
        f.write(os.urandom(size))

def get_file_size(filename):
    try:
        return os.path.getsize(filename)
    except FileNotFoundError:
        print(f"File '{filename}' not found.")
        return None

input_filename = 'input_bytes.bin'
output_filename = 'random_bytes.bin'

size = get_file_size(input_filename)
if size is not None:
    generate_random_bytes(output_filename, size)
    print(f"Generated {size} random bytes and wrote them to '{output_filename}'.")
