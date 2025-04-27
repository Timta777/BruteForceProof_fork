def read_hex_file(filename):
    try:
        with open(filename, 'rb') as file:
            return file.read()
    except FileNotFoundError:
        print(f"File {filename} not found")
        return None

def read_changes_file(filename):
    try:
        with open(filename, 'r') as file:
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

def reverse_changes(changes, random_bytes):
    output = bytearray()
    i = 0
    j = 0
    while i < len(changes):
        if changes[i] == '+':
            hex2 = changes[i+1:i+3]
            hex1 = hex(random_bytes[j])[2:].upper().zfill(2)
            result = calculate(hex1, hex2, '+')
            output.append(result)
            i += 3
            j += 1
        elif changes[i] == '-':
            hex2 = changes[i+1:i+3]
            hex1 = hex(random_bytes[j])[2:].upper().zfill(2)
            result = calculate(hex1, hex2, '-')
            output.append(result)
            i += 3
            j += 1
        else:
            print("Invalid changes file")
            return None
    return output

def main():
    random_bytes = read_hex_file('random_bytes.bin')
    changes = read_changes_file('changes.txt')
    if random_bytes is None or changes is None:
        return

    output = reverse_changes(changes, random_bytes)
    if output is not None:
        with open('reversed_bytes.bin', 'wb') as file:
            file.write(output)

if __name__ == "__main__":
    main()
