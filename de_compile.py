def compile_changes(changes_txt_path, changes_bin_path):
    with open(changes_txt_path, "r") as txt_file, open(changes_bin_path, "wb") as bin_file:
        changes = txt_file.read()
        i = 0
        while i < len(changes):
            op = changes[i]
            hexval = changes[i+1:i+3]
            value = int(hexval, 16)
            # Critical patch for "+00": encode as 0x00 without transformation
            if op == "+" and hexval.upper() == "00":
                out_val = 0x00
            elif op == "+":
                out_val = (value + 0x7F) % 256
            elif op == "-":
                out_val = value
            else:
                raise ValueError(f"Unknown operation {op} at position {i}")
            bin_file.write(bytes([out_val]))
            i += 3

def decompile_changes(changes_bin_path, changes_txt_path):
    with open(changes_bin_path, "rb") as bin_file, open(changes_txt_path, "w") as txt_file:
        i = 0
        while True:
            byte = bin_file.read(1)
            if not byte:
                break
            val = byte[0]
            # Critical patch for "+00" encoding:
            if val == 0x00:
                txt_file.write("+00")
            elif val >= 0x7F:
                orig_val = (val - 0x7F) % 256
                txt_file.write(f"+{orig_val:02X}")
            else:
                txt_file.write(f"-{val:02X}")
            i += 1

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="Compile or decompile changes.txt <-> changes.bin")
    parser.add_argument("mode", choices=["compile", "decompile"], help="Operation mode")
    parser.add_argument("infile", help="Input file (changes.txt or changes.bin)")
    parser.add_argument("outfile", help="Output file (changes.bin or changes.txt)")
    args = parser.parse_args()

    if args.mode == "compile":
        compile_changes(args.infile, args.outfile)
        print(f"Compiled {args.infile} to {args.outfile}")
    else:
        decompile_changes(args.infile, args.outfile)
        print(f"Decompiled {args.infile} to {args.outfile}")
