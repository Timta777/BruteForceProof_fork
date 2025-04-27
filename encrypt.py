def read_hex_file(filename):
    try:
        with open(filename, 'rb') as file:
            return file.read()
    except FileNotFoundError:
        print(f"File {filename} not found")
        return None

def calculate(hex1, hex2, operation):
    num1 = int(hex1, 16)
    num2 = int(hex2, 16)
    if operation == "+":
        result = (num1 + num2) % 256
    elif operation == "-":
        result = (num1 - num2) % 256
    return result

def find_second_value(hex1, result):
    lowest_value = None
    operation = None
    for i in range(256):
        hex2 = hex(i)[2:].upper().zfill(2)
        if calculate(hex1, hex2, '+') == result:
            if lowest_value is None or i < int(lowest_value, 16):
                lowest_value = hex2
                operation = '+'
        if calculate(hex1, hex2, '-') == result:
            if lowest_value is None or i < int(lowest_value, 16):
                lowest_value = hex2
                operation = '-'
    return lowest_value, operation

def main():
    random_bytes = read_hex_file('random_bytes.bin')
    input_bytes = read_hex_file('input_bytes.bin')
    if random_bytes is None or input_bytes is None:
        return

    output = ''
    for i in range(min(len(random_bytes), len(input_bytes))):
        hex1 = hex(random_bytes[i])[2:].upper().zfill(2)
        result = input_bytes[i]
        lowest_value, operation = find_second_value(hex1, result)
        if lowest_value:
            if operation == '+':
                output += f"+{lowest_value}"
            else:
                output += f"-{lowest_value}"
        else:
            output += f"-FF"  # default value if no solution is found

    with open('changes.txt', 'w') as file:
        file.write(output)

if __name__ == "__main__":
    main()
