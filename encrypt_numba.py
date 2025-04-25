import numba
import time
import os

@numba.jit(nopython=True)
def compare_bytes(ib, tb):
    if ib == tb:
        return 0
    elif tb > ib:
        return tb - ib
    else:
        return -(ib - tb)

def generate_changes(input_file, target_file, changes_file):
    """
    Generate a string of changes between two binary files.

    Args:
        input_file (str): The path to the input file.
        target_file (str): The path to the target file.
        changes_file (str): The path to the file where the changes will be written.

    Raises:
        ValueError: If the files are not the same length.
        FileNotFoundError: If one of the files does not exist.
    """
    try:
        with open(input_file, 'rb') as f:
            input_bytes = f.read()

        with open(target_file, 'rb') as f:
            target_bytes = f.read()

        if len(input_bytes) != len(target_bytes):
            raise ValueError("Files must be the same length")

        changes = ''
        start_time = time.time()
        total_bytes = len(input_bytes)
        for i in range(len(input_bytes)):
            ib = input_bytes[i]
            tb = target_bytes[i]

            diff = compare_bytes(ib, tb)
            if diff == 0:
                changes += '&'
            elif diff > 0:
                changes += f"-{diff:02x}"
            else:
                changes += f"+{abs(diff):02x}"

            if i % 1000 == 0:
                elapsed_time = time.time() - start_time
                if elapsed_time > 0:
                    transfer_speed = (i + 1) / elapsed_time / 1024 / 1024  # in MB/s
                    estimated_time = (total_bytes - i - 1) / transfer_speed  # in seconds
                else:
                    transfer_speed = 0
                    estimated_time = 0

                loading_bar = '#' * int(50 * (i + 1) / total_bytes)
                loading_bar += '-' * (50 - len(loading_bar))
                print(f"\r[{loading_bar}] {int(100 * (i + 1) / total_bytes)}% | {transfer_speed:.2f} MB/s | Estimated time: {int(estimated_time // 60):02d}:{int(estimated_time % 60):02d}", end='')

        print()
        with open(changes_file, 'w') as f:
            f.write(changes)

    except FileNotFoundError as e:
        print(f"File not found: {e}")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    generate_changes("random_bytes.bin", "input_bytes.bin", "changes.txt")
