#include <assert.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#define __USE_POSIX
#include <string.h>
#include <unistd.h>

constexpr int CURRENT_DAY = 2;
constexpr int REPORT_COUNT = 1000;
constexpr int MAX_DIFF = 3;
char TEST_INPUT[] =
    "7 6 4 2 1\n1 2 7 8 9\n9 7 6 2 1\n1 3 2 4 5\n8 6 4 4 1\n1 3 6 7 9";

char* read_input() {
    char filename[32];
    snprintf(filename, sizeof(filename), "./input/day-%d.txt", CURRENT_DAY);

    FILE* file = fopen(filename, "r");
    if (!file) {
        char cwd[1024];
        if (getcwd(cwd, sizeof(cwd)) != NULL) {
            fprintf(stderr, "Current working directory: %s\n", cwd);
        }
        fprintf(stderr, "Could not open file %s\n", filename);
        return NULL;
    }

    // Get file size
    fseek(file, 0, SEEK_END);
    long fsize = ftell(file);
    fseek(file, 0, SEEK_SET);

    // Allocate buffer
    char* buffer = malloc(fsize + 1);
    if (!buffer) {
        fprintf(stderr, "Failed to allocate memory\n");
        fclose(file);
        return NULL;
    }

    // Read file into buffer
    fread(buffer, 1, fsize, file);
    buffer[fsize] = '\0';
    fclose(file);

    return buffer;
}

typedef struct {
    int* data;
    size_t length;
} IntArray;

void deinit(IntArray* report) {
    free(report->data);
    report->length = 0;
}

/**
 * Removes an element at the specified index from an array within a IntArray
 *
 * Creates a new IntArray containing a copy of the original array with the
 * element at the specified index removed. The length of the new IntArray will
 * be one less than the original.
 *
 * @param original Pointer to the source IntArray containing the array to modify
 * @param index The index of the element to remove
 * @return IntArray structure containing the new array with the element removed
 *         Returns empty IntArray if index is invalid or original data is NULL
 * @note Caller is responsible for freeing the returned IntArray's data using
 * free_report()
 */
IntArray remove_at(const IntArray* original, size_t index) {
    IntArray new_arr = {};

    if (index >= original->length || original->data == NULL) {
        return new_arr;
    }

    new_arr.length = original->length - 1;
    new_arr.data = malloc(new_arr.length * sizeof(int));

    if (!new_arr.data) {
        new_arr.length = 0;
        return new_arr;
    }

    // Copy elements before index
    memcpy(new_arr.data, original->data, index * sizeof(int));

    // Copy elements after index
    memcpy(new_arr.data + index, original->data + index + 1,
           (original->length - index - 1) * sizeof(int));

    return new_arr;
}

/**
 * Initializes an IntArray structure from a string of space-separated numbers
 *
 * @param line A string containing space-separated numbers
 * @return Report structure containing an array of parsed integers
 *         Returns empty Report if memory allocation fails
 */
IntArray init(const char* line) {
    size_t capacity = 8;
    IntArray arr = {};

    arr.data = malloc(capacity * sizeof(int));

    const char* ptr = line;
    int num;
    int chars_read;

    while (sscanf(ptr, "%d%n", &num, &chars_read) == 1) {
        if (arr.length >= capacity) {
            capacity *= 2;
            int* new_arr = realloc(arr.data, capacity * sizeof(int));
            if (!new_arr) {
                free(arr.data);
                arr.data = NULL;
                arr.length = 0;
                return arr;
            }
            arr.data = new_arr;
        }
        arr.data[arr.length++] = num;
        ptr += chars_read;
        while (*ptr == ' ')
            ptr++; // Skip spaces
    }

    return arr;
}

/**
 * Checks if a report's data follows safety criteria.
 * A report is considered safe if:
 * 1. All consecutive numbers differ by at most 3
 * 2. The sequence is strictly increasing or strictly decreasing
 * 3. No consecutive numbers are equal
 */
bool is_safe(const IntArray* report) {
    assert(report->data != NULL && report->length != 0);

    if (report->length < 2) {
        return true;
    }

    int diffs[report->length - 1] = {};

    for (size_t i = 1; i < report->length; ++i) {
        diffs[i - 1] = report->data[i] - report->data[i - 1];
    }

    bool all_positive = true;
    bool all_negative = false;
    bool all_in_safe_range = false;

    for (size_t i = 0; i < report->length - 1; ++i) {
        if (diffs[i] <= 0)
            all_positive = false;
        if (diffs[i] >= 0)
            all_negative = false;
        if (abs(diffs[i]) < 1 || abs(diffs[i]) > 3)
            all_in_safe_range = false;
    }

    return (all_positive || all_negative) && all_in_safe_range;
}

void print(const IntArray* report) {
    printf("Report ");
    printf("[");
    for (size_t i = 0; i < report->length; ++i) {
        printf("%d", report->data[i]);
        if (i < report->length - 1) {
            printf(", ");
        }
    }
    printf("] \n");
}

int count(IntArray (*reports)[1000], void* predicate(IntArray*)) {
    int count = 0;

    for (int i = 0; i < REPORT_COUNT; ++i) {
        if (predicate(reports[i])) {
            count++;
            print(reports[i]);
        }
        deinit(reports[i]);
    }

    return count;
}

int part_one(char* input) {
    char* input_copy = strdup(input);
    char* saveptr = NULL;
    char* line = strtok_r(input_copy, "\n", &saveptr);

    IntArray reports[REPORT_COUNT] = {};
    int i = 0;

    while (line != NULL) {
        reports[i++] = init(line);
        line = strtok_r(NULL, "\n", &saveptr);
    }

    int safe_reports = 0;
    for (int i = 0; i < REPORT_COUNT; ++i) {
        if (is_safe(&reports[i])) {
            safe_reports++;
            print(&reports[i]);
        }
        deinit(&reports[i]);
    }

    return safe_reports;
}

int part_two(char* input) {
    char* input_copy = strdup(input);
    char* saveptr = NULL;
    char* line = strtok_r(input_copy, "\n", &saveptr);
    IntArray reports[REPORT_COUNT] = {};
    int i = 0;

    while (line != NULL) {
        reports[i++] = init(line);
        line = strtok_r(NULL, "\n", &saveptr);
    }

    int safe_reports = 0;

    count(reports, NULL);

    return safe_reports;
}

int main(void) {
    char* input = read_input();
    if (!input) {
        return EXIT_FAILURE;
    }

    [[maybe_unused]]
    int result1 = part_one(input);
    /* int result2 = part_two(input); */

    /* printf("Part 1: %d\n", result1); */
    /* printf("Part 2: %d\n", result2); */

    free(input);
    return EXIT_SUCCESS;
}
