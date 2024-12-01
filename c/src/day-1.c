#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

constexpr int CURRENT_DAY = 1;

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

constexpr int MAX_COLUMNS = 1000;

typedef struct {
    int left[MAX_COLUMNS];
    int right[MAX_COLUMNS];
} Columns;

void columns_push_left(Columns* columns, int value) {
    for (int i = 0; i < MAX_COLUMNS; ++i) {
        if (columns->left[i] == 0) {
            columns->left[i] = value;
            break;
        }
    }
}

void columns_push_right(Columns* columns, int value) {
    for (int i = 0; i < MAX_COLUMNS; ++i) {
        if (columns->right[i] == 0) {
            columns->right[i] = value;
            break;
        }
    }
}

// Initialize columns from input string
void columns_init(Columns* cols, char* input) {
    char* input_copy = strdup(input);
    if (!input_copy) {
        return;
    }

    char* line = strtok(input_copy, "\n");

    while (line != NULL) {
        int left, right;
        sscanf(line, "%d%d", &left, &right);
        columns_push_left(cols, left);
        columns_push_right(cols, right);

        line = strtok(NULL, "\n");
    }

    free(input_copy);
}

void columns_print(const Columns* cols) {
    for (int i = 0; i < MAX_COLUMNS; ++i) {
        if (cols->left[i] == 0 || cols->right[i] == 0) {
            break;
        }
        printf("Left: %d Right: %d\n", cols->left[i], cols->right[i]);
    }
}

int compare_ints(const void* a, const void* b) { return (*(int*)a - *(int*)b); }

void columns_sort(Columns* cols) {
    qsort(cols->left, MAX_COLUMNS, sizeof(int), compare_ints);
    qsort(cols->right, MAX_COLUMNS, sizeof(int), compare_ints);
}

void columns_distances(Columns* cols, int distances[MAX_COLUMNS]) {
    for (int i = 0; i < MAX_COLUMNS; ++i) {
        distances[i] = abs(cols->right[i] - cols->left[i]);
    }
}

// Returns the number of occurrences of a value in the right column
int columns_occurrences_r(const Columns* cols, int value) {
    int count = 0;
    for (int i = 0; i < MAX_COLUMNS; ++i) {
        if (cols->right[i] == value) {
            count++;
        }
    }
    return count;
}

int columns_similarity_score(const Columns* cols) {
    int similarity_score = 0;

    for (int i = 0; i < MAX_COLUMNS; ++i) {
        int l_val = cols->left[i];
        int occurrences = columns_occurrences_r(cols, l_val);
        similarity_score += occurrences * l_val;
    }

    return similarity_score;
}

int sum_distances(const int distances[MAX_COLUMNS]) {
    int sum = 0;
    for (int i = 0; i < MAX_COLUMNS; ++i) {
        sum += distances[i];
    }
    return sum;
}

int part_one(char* input) {
    Columns cols = {0};
    columns_init(&cols, input);

    columns_sort(&cols);

    int distances[MAX_COLUMNS] = {0};
    columns_distances(&cols, distances);

    int sum = sum_distances(distances);

    return sum;
}

int part_two(char* input) {
    Columns cols = {0};
    columns_init(&cols, input);

    int similarity_score = columns_similarity_score(&cols);

    return similarity_score;
}

int main(void) {
    char* input = read_input();
    if (!input) {
        return EXIT_FAILURE;
    }

    int result1 = part_one(input);
    int result2 = part_two(input);

    printf("Part 1: %d\n", result1);
    printf("Part 2: %d\n", result2);

    free(input);
    return EXIT_SUCCESS;
}
