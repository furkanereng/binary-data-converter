# Binary Data Converter
This is a simple program that converts the given hexadecimal data in the memory to a signed/unsigned integer or a floating point number. This is the first project assignment of Systems Programming course.

- The content of the memory must be given in an input file. Each line of the input file must include 12 bytes of hexadecimal numbers those are separated by a blank.
- The allowed data type sizes are `1`, `2`, `3` and `4` bytes.
- User can choose the byte ordering type as Little Endian `l` or Big Endian `b` presentations.
- The allowed data types to convert are Unsigned Integer `u`, Signed Integer `i` and Floating Point `fp`.
- For the integer literals, Two's Compelement representation is used.
- For the floating point literals an IEEE-like format is used, where number of exponent bits are defined inside the program for each data size. Also, round to even method is used for rounding fraction bits to 13 bits.

# How to Run?
This program can be runned after it is compiled, from the command-line with the following format:
```
java BinaryDataConverter <Input file name> <Byte ordering type: 'l' or 'b'> <Data type: 'u' or 'i' or 'fp'> <Data size in bytes: 1, 2, 3, 4>
java BinaryDataConverter input.txt l fp 4
```
