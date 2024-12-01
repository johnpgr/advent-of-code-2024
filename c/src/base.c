#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

constexpr int CURRENT_DAY = 0;

char* read_input() {
    char filename[32];
    snprintf(filename, sizeof(filename), "./input/%d.txt", CURRENT_DAY);

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

int part_1(char* input [[maybe_unused]]) { return 0; }

int part_2(char* input [[maybe_unused]]) { return 0; }

int main(void) {
    char* input = read_input();
    if (!input) {
        return EXIT_FAILURE;
    }
    printf("input = %s\n", input);

    int result1 = part_1(input);
    int result2 = part_2(input);

    printf("Part 1: %d\n", result1);
    printf("Part 2: %d\n", result2);

    free(input);
    return EXIT_SUCCESS;
}
