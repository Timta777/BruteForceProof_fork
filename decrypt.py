def reverse_changes(base_bytes, changes_str):
    result = bytearray(base_bytes)
    byte_index = 0  # Start at the first byte
    i = 0  # Position in the changes string

    while i < len(changes_str):
        change_type = changes_str[i]
        
        if change_type == "&":
            # Skip the byte, just move to the next change
            byte_index += 1
            i += 1
        else:
            # For + or -, extract the hex value following the change type
            hex_val = changes_str[i+1:i+3]
            value = int(hex_val, 16)

            if change_type == "+":
                # Reverse the addition by subtracting the value
                result[byte_index] = (result[byte_index] - value) % 256
            elif change_type == "-":
                # Reverse the subtraction by adding the value
                result[byte_index] = (result[byte_index] + value) % 256
            
            # Move to the next byte and skip to the next change
            byte_index += 1
            i += 3  # Skip the type and the two hexadecimal digits
    
    return result

def main():
    # Read the base file (random_bytes.bin)
    with open("random_bytes.bin", "rb") as f:
        base_bytes = f.read()

    # Read the changes string from the changes.txt file
    with open("changes.txt", "r") as f:
        changes_str = f.read().strip()

    # Apply the reverse changes
    result = reverse_changes(base_bytes, changes_str)

    # Write the modified bytes to the output file
    with open("output_bytes.bin", "wb") as f:
        f.write(result)

if __name__ == "__main__":
    main()
