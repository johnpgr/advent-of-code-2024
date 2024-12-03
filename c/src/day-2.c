#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#define __USE_POSIX
#include <string.h>
#include <unistd.h>

constexpr int CURRENT_DAY = 2;
constexpr int REPORT_COUNT = 1000;
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
} Report;

void free_report(Report* report) {
    free(report->data);
    report->length = 0;
}

/**
 * Converts a line of space-separated numbers into a Report structure
 *
 * @param line A string containing space-separated numbers
 * @return Report structure containing an array of parsed integers
 *         Returns empty Report if memory allocation fails
 */
Report report_from_line(const char* line) {
    size_t capacity = 8;
    Report report = {.data = NULL, .length = 0};

    report.data = malloc(capacity * sizeof(int));

    const char* ptr = line;
    int num;
    int chars_read;

    while (sscanf(ptr, "%d%n", &num, &chars_read) == 1) {
        if (report.length >= capacity) {
            capacity *= 2;
            int* new_arr = realloc(report.data, capacity * sizeof(int));
            if (!new_arr) {
                free(report.data);
                report.data = NULL;
                report.length = 0;
                return report;
            }
            report.data = new_arr;
        }
        report.data[report.length++] = num;
        ptr += chars_read;
        while (*ptr == ' ')
            ptr++; // Skip spaces
    }

    return report;
}

/**
 * Checks if a report's data follows safety criteria.
 * A report is considered safe if:
 * 1. All consecutive numbers differ by at most 3
 * 2. The sequence is strictly increasing or strictly decreasing
 * 3. No consecutive numbers are equal
 */
bool report_is_safe(const Report* report) {
    assert(report->data != NULL && report->length != 0);

    if (report->length < 2) {
        return true;
    }

    constexpr int MAX_DIFF = 3;
    int first_diff = report->data[1] - report->data[0];
    bool is_increasing = first_diff > 0 ? true : false;

    if(first_diff == 0) {
        return false;
    }

    for (size_t i = 2; i < report->length; ++i) {
        int current_diff = report->data[i] - report->data[i - 1];
        bool current_is_increasing = current_diff > 0 ? true : false;

        if(current_diff == 0) {
            return false;
        }

        if(abs(current_diff) > MAX_DIFF) {
            return false;
        }

        if (current_is_increasing != is_increasing) {
            return false;
        }
    }

    return true;
}

void report_print(const Report* report) {
    printf("Report: ");
    printf("[");
    for (size_t i = 0; i < report->length; ++i) {
        printf("%d", report->data[i]);
        if (i < report->length - 1) {
            printf(", ");
        }
    }
    printf("] ");
    printf("length: %zu\n", report->length);
}

int part_one(char* input) {
    Report reports[REPORT_COUNT] = {};
    char* saveptr = NULL;
    char* line = strtok_r(input, "\n", &saveptr);

    int i = 0;
    while (line != NULL) {
        reports[i++] = report_from_line(line);
        line = strtok_r(NULL, "\n", &saveptr);
    }

    int safe_reports = 0;
    for (int i = 0; i < REPORT_COUNT; ++i) {
        if (report_is_safe(&reports[i])) {
            safe_reports++;
            report_print(&reports[i]);
        }
        free_report(&reports[i]);
    }

    return safe_reports;
}

int part_two(char* input [[maybe_unused]]) { return 0; }

int main(void) {
    char* input = read_input();
    if (!input) {
        return EXIT_FAILURE;
    }

    int result1 = part_one(input);
    /* int result2 = part_two(input); */

    printf("Part 1: %d\n", result1);
    /* printf("Part 2: %d\n", result2); */

    /* free(input); */
    return EXIT_SUCCESS;
}
