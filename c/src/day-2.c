#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#define __USE_POSIX
#include <string.h>
#include <unistd.h>

constexpr int CURRENT_DAY = 2;

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
 * Checks if a report's numerical sequence is monotonically increasing or decreasing.
 *
 * @param report Pointer to the Report structure containing the sequence to check.
 *              The report must contain a valid data array and length.
 *
 * @return true if:
 *         - The report has less than 2 elements
 *         - All elements maintain the same direction (increasing or decreasing)
 *         false if the sequence changes direction at any point
 *
 * Example:
 *   [1,2,3,4] -> true (monotonically increasing)
 *   [4,3,2,1] -> true (monotonically decreasing)
 *   [1,2,1,3] -> false (changes direction)
 */
bool report_is_safe(const Report* report) {
    assert(report->data != NULL && report->length > 0 && "Report is uninitialized");

    if (report->length < 2) {
        return true;
    }

    bool is_increasing = report->data[1] > report->data[0];

    for (size_t i = 2; i < report->length; ++i) {
        bool current_increasing = report->data[i] > report->data[i - 1];
        if (current_increasing != is_increasing) {
            return false;
        }
    }

    return true;
}

void report_print(const Report* report) {
    printf("[");
    for (size_t i = 0; i < report->length; ++i) {
        printf("%d", report->data[i]);
        if (i < report->length - 1) {
            printf(", ");
        }
    }
    printf("]\n");
}

int part_one(char* input) {
    Report reports[1000] = {0};
    char* saveptr = NULL;
    char* line = strtok_r(input, "\n", &saveptr);

    int i = 0;
    while (line != NULL) {
        reports[i++] = report_from_line(line);
        line = strtok_r(NULL, "\n", &saveptr);
    }

    int safe_reports = 0;
    for(int i = 0; i < 1000; ++i) {
        if (report_is_safe(&reports[i])) {
            safe_reports++;
            /* printf("Safe report: "); */
            /* report_print(&reports[i]); */
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
