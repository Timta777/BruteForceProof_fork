import numba
import os

@numba.jit(nopython=True)
def compare_bytes(input_bytes, target_bytes):
    changes = []
    for i in range(len(input_bytes)):
        ib = input_bytes[i]
        tb = target_bytes[i]

        if ib == tb:
            changes.append(0)  # No change if the bytes are equal
        elif tb > ib:
            changes.append(tb - ib)  # Target byte is greater
        else:
            changes.append(-(ib - tb))  # Input byte is greater

    return changes

def generate_changes(input_file, target_file, changes_file):
    try:
        # Read the input and target files as binary
        with open(input_file, 'rb') as f:
            input_bytes = bytearray(f.read())
        with open(target_file, 'rb') as f:
            target_bytes = bytearray(f.read())

        # Check if the files have the same length
        if len(input_bytes) != len(target_bytes):
            raise ValueError("Files must be the same length")

        # Compare the bytes using Numba
        changes = compare_bytes(input_bytes, target_bytes)

        # Convert the changes to a string
        changes_str = ''
        for change in changes:
            if change == 0:
                changes_str += '&'  # No change if the bytes are equal
            elif change > 0:
                changes_str += f"-{change:02x}"  # Target byte is greater
            else:
                changes_str += f"+{abs(change):02x}"  # Input byte is greater

        # Write the changes to the file in one line
        with open(changes_file, 'w') as f:
            f.write(changes_str)

    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    input_bytes_file = "random_bytes.bin"
    target_bytes_file = "input_bytes.bin"
    changes_file = "changes.txt"

    generate_changes(input_bytes_file, target_bytes_file, changes_file)
