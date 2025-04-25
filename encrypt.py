def generate_changes(input_file, target_file, changes_file):
    # Read the input and target files as binary
    with open(input_file, 'rb') as f:
        input_bytes = f.read()

    with open(target_file, 'rb') as f:
        target_bytes = f.read()

    # Check if the files have the same length
    if len(input_bytes) != len(target_bytes):
        raise ValueError("Files must be the same length")

    # Initialize an empty string to store changes
    changes = ''

    # Compare the bytes
    for i in range(len(input_bytes)):
        ib = input_bytes[i]
        tb = target_bytes[i]

        if ib == tb:
            changes += '&'  # No change if the bytes are equal
        elif tb > ib:
            changes += f"-{(tb - ib):02x}"  # Target byte is greater
        else:
            changes += f"+{(ib - tb):02x}"  # Input byte is greater

    # Write the changes to the file in one line
    with open(changes_file, 'w') as f:
        f.write(changes)

if __name__ == "__main__":
    generate_changes("random_bytes.bin", "input_bytes.bin", "changes.txt")
