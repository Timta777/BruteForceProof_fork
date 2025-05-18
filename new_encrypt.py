import numpy as np

def read_hex_file(filename):
    try:
        with open(filename, 'rb') as file:
            return np.frombuffer(file.read(), dtype=np.uint8)
    except FileNotFoundError:
        print(f"File {filename} not found")
        return None

def calculate(hex1, hex2, operation):
    if operation == "+":
        return (hex1 + hex2) % 256
    elif operation == "-":
        return (hex1 - hex2) % 256

def find_second_value(hex1, result):
    # Cast both to uint16 to avoid overflow during operation
    hex1 = np.uint16(hex1)
    result = np.uint16(result)
    hex2 = np.arange(256, dtype=np.uint16)
    add_results = (hex1 + hex2) % 256
    sub_results = (hex1 - hex2) % 256

    add_match = np.where(add_results == result)[0]
    sub_match = np.where(sub_results == result)[0]

    lowest_value = None
    operation = None
    if add_match.size > 0:
        lowest_value = add_match[0]
        operation = '+'
    if sub_match.size > 0:
        if lowest_value is None or sub_match[0] < lowest_value:
            lowest_value = sub_match[0]
            operation = '-'
    if lowest_value is not None:
        return f"{lowest_value:02X}", operation
    return None, None

def main():
    random_bytes = read_hex_file('random_bytes.bin')
    input_bytes = read_hex_file('input_bytes.bin')
    if random_bytes is None or input_bytes is None:
        return

    output = []
    length = min(len(random_bytes), len(input_bytes))
    for i in range(length):
        hex1 = random_bytes[i]
        result = input_bytes[i]
        lowest_value, operation = find_second_value(hex1, result)
        if lowest_value:
            if operation == '+':
                output.append(f"+{lowest_value}")
            else:
                output.append(f"-{lowest_value}")
        else:
            output.append("-FF")  # default value if no solution is found

    with open('changes.txt', 'w') as file:
        file.write(''.join(output))

if __name__ == "__main__":
    main()
