#!/bin/bash

# Default to current date
CURRENT_YEAR=$(date +"%Y")
DAY=$(date +"%d")
#Remove prefix 0 from day
DAY=${DAY#0}

# Parse arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        -d|--day) DAY="$2"; shift ;;
        -y|--year) CURRENT_YEAR="$2"; shift ;;
        *) OUTPUT_FILE="$1" ;;
    esac
    shift
done

# Default output file if not provided
if [ -z "$OUTPUT_FILE" ]; then
    OUTPUT_FILE="./app/src/main/resources/input/day-$DAY.txt"
    # Create src directory if it doesn't exist
    mkdir -p ./input
fi

if [ ! -f .token ]; then
    echo "Error: .token file not found"
    exit 1
fi

URL="https://adventofcode.com/$CURRENT_YEAR/day/$DAY/input"
TOKEN=$(cat .token)

curl --cookie "session=$TOKEN" \
    "$URL" \
    -o "$OUTPUT_FILE"
